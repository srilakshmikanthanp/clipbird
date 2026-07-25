package com.srilakshmikanthanp.clipbird.peer

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.packet.*
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.cancellation.CancellationException

open class ChannelHub<P: PairedDevice>(
  private val pairedDeviceService: PairedDeviceService<P>,
  private val scope: CoroutineScope
) {
  private val _devices = MutableStateFlow<List<DeviceState>>(emptyList())
  val devices: StateFlow<List<PairedDevice>> = _devices.map {
    devices -> devices.map { it.connectedDevice.device }
  }.stateIn(
    scope,
    SharingStarted.Eagerly,
    emptyList()
  )

  private val packetInterceptor: PacketInterceptor = PacketInterceptors(
    PacketDeduplicator(),
    PacketReRouter { _devices.value.map { it.connectedDevice.channel } }
  )

  private val _clipboard = MutableSharedFlow<ClipboardDatum>()
  val clipboard: Flow<ClipboardDatum> = _clipboard.asSharedFlow()

  private val mutex = Mutex()

  private data class DeviceState(
    val connectedDevice: ConnectedDevice,
    val readerJob: Job
  ) : AutoCloseable {
    override fun close() {
      readerJob.cancel()
      connectedDevice.channel.closeQuietly()
    }
  }

  private suspend fun handleInvalidPacket(connectedDevice: ConnectedDevice, packet: Packet) {
    try {
      Logger.w("Invalid packet received from ${connectedDevice.device.name}: $packet", null, TAG)
      connectedDevice.channel.sendPacket(ErrorPacket(ErrorCode.INVALID_PACKET, "Invalid packet"))
    } catch (e: Exception) {
      Logger.e("Failed to send error packet to ${connectedDevice.device.name}: ${e.message}", e, TAG)
    } finally {
      this.remove(connectedDevice)
    }
  }

  private suspend fun observeChannelPackets(connectedDevice: ConnectedDevice) {
    try {
      connectedDevice.channel.readPackets(packetInterceptor).collect { dispatchPacket(it) }
    } catch (e: CancellationException) {
      throw e
    } catch (e: PeerException) {
      this.handleInvalidPacket(connectedDevice, e.toErrorPacket())
    } catch (e: Exception) {
      Logger.e("Error while reading packets from ${connectedDevice.device.name}: ${e.message}", e, TAG)
    } finally {
      this.remove(connectedDevice)
    }
  }

  private suspend fun observePairedDevices() {
    pairedDeviceService.getAll().map {
      paired -> paired.map { it.id }.toSet()
    }.collect { pairedDeviceIds ->
      val stale = mutex.withLock {
        val stale = _devices.value.filter { it.connectedDevice.device.id !in pairedDeviceIds }
        _devices.value = _devices.value.filter { it.connectedDevice.device.id in pairedDeviceIds }
        stale
      }

      stale.forEach {
        it.close()
      }
    }
  }

  private suspend fun remove(connectedDevice: ConnectedDevice) {
    val removed = mutex.withLock {
      val removed = _devices.value.find { it.connectedDevice.device.id == connectedDevice.device.id }
      _devices.value = _devices.value.filter { it.connectedDevice.device.id != connectedDevice.device.id }
      removed
    }

    removed?.close()
  }

  private suspend fun dispatchPacket(packet: Packet) {
    when (packet) {
      is ClipboardSyncingPacket -> _clipboard.emit(packet.datum)
      else -> throw PeerException(ErrorCode.INVALID_PACKET, "Invalid packet")
    }
  }

  suspend fun add(connectedDevice: ConnectedDevice) {
    val duplicate = mutex.withLock {
      if (_devices.value.none { it.connectedDevice.device.id == connectedDevice.device.id }) {
        val job = scope.launch { observeChannelPackets(connectedDevice) }
        _devices.value += DeviceState(connectedDevice, job)
        false
      } else {
        true
      }
    }

    if (!duplicate) {
      return
    }

    try {
      Logger.w("Duplicate connection for ${connectedDevice.device.name}, closing new channel", null, TAG)
      connectedDevice.channel.sendPacket(ErrorPacket(ErrorPacket.ErrorCode.ALREADY_CONNECTED, "Duplicate connection"))
    } catch (e: Exception) {
      Logger.e("Failed to send error packet to ${connectedDevice.device.name}: ${e.message}", e, TAG)
    } finally {
      connectedDevice.channel.closeQuietly()
    }
  }

  suspend fun run() {
    try {
      observePairedDevices()
    } finally {
      val devices = _devices.value
      _devices.value = emptyList()
      devices.forEach(DeviceState::close)
    }
  }

  companion object {
    const val TAG = "ChannelHub"
  }
}
