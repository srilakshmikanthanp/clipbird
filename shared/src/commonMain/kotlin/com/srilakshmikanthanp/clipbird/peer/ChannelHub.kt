package com.srilakshmikanthanp.clipbird.peer

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.closeQuietly
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChannelHub(
  private val pairedDeviceService: PairedDeviceService<out PairedDevice>,
  private val scope: CoroutineScope,
) {
  private val _devices = MutableStateFlow<List<ConnectedDevice>>(emptyList())
  val devices: StateFlow<List<ConnectedDevice>> = _devices.asStateFlow()
  private val mutex = Mutex()

  private var job: Job? = null

  private suspend fun observePairedDevices() {
    pairedDeviceService.getAll().map {
      paired -> paired.map { it.id }.toSet()
    }.collect { pairedDeviceIds ->
      val stale = mutex.withLock {
        val stale = _devices.value.filter { it.device.id !in pairedDeviceIds }
        _devices.value = _devices.value.filter { it.device.id in pairedDeviceIds }
        stale
      }

      stale.forEach {
        it.channel.closeQuietly()
      }
    }
  }

  suspend fun add(incoming: ConnectedDevice) {
    val duplicate = mutex.withLock {
      if (_devices.value.none { it.device.id == incoming.device.id }) {
        _devices.value += incoming
        false
      } else {
        true
      }
    }

    if (!duplicate) {
      return
    }

    try {
      Logger.w("Duplicate connection for ${incoming.device.name}, closing new channel", null, TAG)
      incoming.channel.sendPacket(ErrorPacket(ErrorPacket.ErrorCode.ALREADY_CONNECTED, "Duplicate connection"))
    } catch (e: Exception) {
      Logger.e("Failed to send error packet to ${incoming.device.name}: ${e.message}", e, TAG)
    } finally {
      incoming.channel.closeQuietly()
    }
  }

  fun start() {
    job = scope.launch {
      observePairedDevices()
    }
  }

  fun stop() {
    job?.cancel()
    job = null
  }

  companion object {
    const val TAG = "ChannelHub"
  }
}
