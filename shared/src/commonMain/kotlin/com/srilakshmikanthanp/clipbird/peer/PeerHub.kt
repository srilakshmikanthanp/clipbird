package com.srilakshmikanthanp.clipbird.peer

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.io.ProgressListener
import com.srilakshmikanthanp.clipbird.packet.*
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketDeduplicator
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketInterceptor
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketInterceptors
import com.srilakshmikanthanp.clipbird.packet.interceptor.PacketReRouter
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import kotlin.collections.any
import kotlin.coroutines.cancellation.CancellationException

open class PeerHub<P: PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<P>,
  private val scope: CoroutineScope
) {
  private val _devices = MutableStateFlow<Map<Long, PeerConnection>>(emptyMap())
  val devices: StateFlow<List<PairedDevice>> = _devices.map {
    devices -> devices.values.map { it.device }
  }.stateIn(
    scope,
    SharingStarted.Eagerly,
    emptyList()
  )

  private val devicesMutex = Mutex()
  private val sendMutex = Mutex()

  private val _transferState = MutableStateFlow<TransferState>(TransferState.Success)
  val transferState: StateFlow<TransferState> = _transferState.asStateFlow()

  private val packetInterceptor: PacketInterceptor = PacketInterceptors(
    PacketDeduplicator(),
    PacketReRouter { _devices.value.values.map { it.channel } }
  )

  private val _clipboard = MutableSharedFlow<ClipboardContent>()
  val clipboard: Flow<ClipboardContent> = _clipboard.asSharedFlow()

  private var job: Job? = null

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
      connection.close()
    }
  }

  suspend fun sendClipboard(clipboardContent: ClipboardContent): Unit = sendMutex.withLock {
    if (clipboardContent.items.isEmpty()) return
    try {
      val listener = ProgressListener { p, t -> _transferState.value = TransferState.Progress(p, t) }
      _transferState.value = TransferState.Progress(0, 0)
      val connections = devicesMutex.withLock { _devices.value.values.toList() }
      val packet = ClipboardSyncingPacket.create(clipboardContent)
      connections.forEach { it.channel.sendPacket(packet, listener) }
      _transferState.value = TransferState.Success
    } catch (e: CancellationException) {
      _transferState.value = TransferState.Success
      throw e
    } catch (e: Exception) {
      _transferState.value = TransferState.Failure(e)
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
      stale.close()
    }

    scope.launch {
      observeChannelPackets(connection)
    }
  }

  fun isConnected(device: PairedDevice): Boolean {
    return _devices.value.any { it.key == device.id }
  }

  fun start() {
    if (job?.isActive == true) return
    job = scope.launch { observePaired() }
  }

  fun stop() {
    _devices.value.values.forEach(PeerConnection::close)
    _devices.value = emptyMap()
    job?.cancel()
  }

  companion object {
    const val TAG = "PeerHub"
  }
}
