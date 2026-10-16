package com.srilakshmikanthanp.clipbird.peer

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.packet.*
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketDeduplicator
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketExpiry
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketInterceptor
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketInterceptors
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketReRouter
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import kotlin.collections.any
import kotlin.coroutines.cancellation.CancellationException

open class PeerHub<P: PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<P>,
  private val scope: CoroutineScope
) {
  private val _devices = MutableStateFlow<Map<ULong, PeerConnection>>(emptyMap())
  val devices: StateFlow<List<PairedDevice>> = _devices.map {
    devices -> devices.values.map { it.device }
  }.stateIn(
    scope,
    SharingStarted.Eagerly,
    emptyList()
  )

  private val devicesMutex = Mutex()
  private val sendMutex = Mutex()

  private val _deviceTransfers = MutableStateFlow<Map<ULong, DeviceTransfer>>(emptyMap())
  val deviceTransfers: StateFlow<Map<ULong, DeviceTransfer>> = _deviceTransfers.asStateFlow()

  val isSending: StateFlow<Boolean> = _deviceTransfers.map { transfers ->
    transfers.values.any { it.state is TransferState.Progress }
  }.stateIn(
    scope,
    SharingStarted.Eagerly,
    false
  )

  private val packetInterceptor: PacketInterceptor = PacketInterceptors(
    PacketExpiry(),
    PacketDeduplicator(),
    PacketReRouter { _devices.value.values.map { it.channel } }
  )

  private val _clipboard = MutableSharedFlow<ClipboardContent>()
  val clipboard: Flow<ClipboardContent> = _clipboard.asSharedFlow()

  private var hubScope: CoroutineScope? = null
  private var hubJob: Job? = null

  private suspend fun observeChannelPackets(peerConnection: PeerConnection) {
    try {
      peerConnection.channel.readPackets(packetInterceptor).collect(::onPacket)
    } catch (e: CancellationException) {
      throw e
    } catch (e: PeerException) {
      peerConnection.channel.trySendPacket(e.toErrorPacket())
    } catch (e: IOException) {
      Logger.i("Connection to ${peerConnection.device.name} closed: ${e.message}", null, TAG)
    } catch (e: Exception) {
      Logger.e("Error while reading packets from ${peerConnection.device.name}: ${e.message}", e, TAG)
    } finally {
      remove(peerConnection)
    }
  }

  private suspend fun onClipboardPacket(packet: ClipboardSyncingPacket) {
    if (packet.content.items.isNotEmpty()) {
      _clipboard.emit(packet.content)
    }
  }

  private suspend fun onPacket(packet: Packet) {
    when (packet) {
      is ClipboardSyncingPacket -> onClipboardPacket(packet)
      else -> throw PeerException(ErrorCode.INVALID_PACKET, "Invalid packet")
    }
  }

  private suspend fun observePaired() {
    pairedDeviceService.getAll().map {
      paired -> paired.map { it.id }.toSet()
    }.collect { pairedDeviceIds ->
      devicesMutex.withLock {
        _devices.value.filterKeys { it !in pairedDeviceIds }.values.toList()
      }.forEach {
        remove(it)
      }
    }
  }

  private suspend fun remove(connection: PeerConnection) {
    devicesMutex.withLock {
      if (_devices.value[connection.device.id] === connection) {
        _devices.value -= connection.device.id
      }
    }.also {
      connection.closeQuietly()
    }
  }

  private suspend fun sendTo(connection: PeerConnection, packet: Packet) {
    val setDeviceTransfer = { device: PairedDevice, state: TransferState ->
      _deviceTransfers.update { it + (device.id to DeviceTransfer(device, state)) }
    }
    try {
      connection.channel.sendPacket(packet) { p, t -> setDeviceTransfer(connection.device, TransferState.Progress(p, t)) }
      setDeviceTransfer(connection.device, TransferState.Success)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.w("Failed to send clipboard to ${connection.device.name}: ${e.message}", e, TAG)
      setDeviceTransfer(connection.device, TransferState.Failure(e))
    }
  }

  suspend fun sendClipboard(clipboardContent: ClipboardContent): Unit = sendMutex.withLock {
    if (clipboardContent == ClipboardContent.Empty) return

    val connections = devicesMutex.withLock { _devices.value.values.toList() }

    _deviceTransfers.value = connections.associate {
      it.device.id to DeviceTransfer(it.device, TransferState.Progress(0, 0))
    }

    try {
      val packet = ClipboardSyncingPacket.create(clipboardContent)
      connections.forEach { sendTo(it, packet) }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.e("Failed to send clipboard: ${e.message}", e, TAG)
    } finally {
      _deviceTransfers.update { transfers ->
        transfers.mapValues { (_, transfer) ->
          if (transfer.state is TransferState.Progress) transfer.copy(state = TransferState.Failure()) else transfer
        }
      }
    }
  }

  suspend fun consume(connection: PeerConnection) {
    val stale = devicesMutex.withLock {
      val present = _devices.value[connection.device.id]
      _devices.value += connection.device.id to connection
      present
    }

    if (stale != null) {
      Logger.w("Replacing stale connection for ${connection.device.name}", null, TAG)
      stale.closeQuietly()
    }

    hubScope?.launch {
      observeChannelPackets(connection)
    }
  }

  fun isConnected(device: PairedDevice): Boolean {
    return _devices.value.any { it.key == device.id }
  }

  fun start() {
    if (hubJob?.isActive == true) return
    val supervisorJob = SupervisorJob(scope.coroutineContext[Job])
    hubScope = CoroutineScope(scope.coroutineContext + supervisorJob)
    hubJob = supervisorJob
    hubScope!!.launch { observePaired() }
  }

  fun stop() {
    _devices.value.values.forEach(PeerConnection::closeQuietly)
    _devices.value = emptyMap()
    val j = hubJob
    hubScope = null
    hubJob = null
    runBlocking { j?.cancelAndJoin() }
  }

  companion object {
    const val TAG = "PeerHub"
  }
}
