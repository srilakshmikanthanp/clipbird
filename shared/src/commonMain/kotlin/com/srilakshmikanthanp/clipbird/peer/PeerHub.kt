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
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.cancellation.CancellationException

open class PeerHub<P: PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<P>,
  private val scope: CoroutineScope
): AutoCloseable {
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

  private suspend fun onPacket(packet: Packet) {
    when (packet) {
      is ClipboardSyncingPacket -> _clipboard.emit(packet.content)
      else -> throw PeerException(ErrorCode.INVALID_PACKET, "Invalid packet")
    }
  }

  private suspend fun observeChannelPackets(peerConnection: PeerConnection) {
    try {
      peerConnection.channel.readPackets(packetInterceptor).collect(::onPacket)
    } catch (e: CancellationException) {
      throw e
    } catch (e: PeerException) {
      peerConnection.channel.trySendPacket(e.toErrorPacket())
    } catch (e: Exception) {
      Logger.e("Error while reading packets from ${peerConnection.device.name}: ${e.message}", e, TAG)
    } finally {
      devicesMutex.withLock { _devices.value -= peerConnection.device.id }
      peerConnection.close()
    }
  }

  private suspend fun observePaired() {
    pairedDeviceService.getAll().map {
      paired -> paired.map { it.id }.toSet()
    }.collect { pairedDeviceIds ->
      val stale = devicesMutex.withLock {
        val stale = _devices.value.filterKeys { it !in pairedDeviceIds }.values.toList()
        _devices.value = _devices.value.filterKeys { it in pairedDeviceIds }
        stale
      }

      stale.forEach(PeerConnection::close)
    }
  }

  suspend fun sendClipboard(clipboardContent: ClipboardContent) {
    sendMutex.withLock {
      val listener = ProgressListener { p, t -> _transferState.value = TransferState.Progress(p, t) }
      _transferState.value = TransferState.Progress(0, 0)
      try {
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
  }

  suspend fun consume(connection: PeerConnection) {
    val added = devicesMutex.withLock {
      val present = _devices.value[connection.device.id]
      if (present == null) _devices.value += connection.device.id to connection
      present == null
    }

    if (added) {
      scope.launch { observeChannelPackets(connection) }
      return
    }

    connection.use { connection ->
      Logger.w("Duplicate connection for ${connection.device.name}, closing new channel", null, TAG)
      connection.channel.trySendPacket(ErrorPacket(ErrorCode.ALREADY_CONNECTED, "Duplicate connection"))
    }
  }

  override fun close() {
    _devices.value.values.forEach(PeerConnection::close)
    _devices.value = emptyMap()
  }

  init {
    scope.launch { observePaired() }
  }

  companion object {
    const val TAG = "PeerHub"
  }
}
