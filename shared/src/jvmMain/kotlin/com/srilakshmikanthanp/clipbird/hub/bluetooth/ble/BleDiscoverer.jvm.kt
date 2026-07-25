package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.ffi.discoverer.ble.BleDiscovererListener
import com.srilakshmikanthanp.clipbird.hub.Discoverer
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.DiscoveryException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual class BleDiscoverer actual constructor(
  private val serviceUuid: Uuid,
  private val deviceTimeout: Duration,
) : Discoverer<BleHubDevice> {
  override val events: Flow<DiscoveryEvent<BleHubDevice>> = channelFlow {
    val devices = mutableMapOf<Long, SeenDevice>()
    val channel = Channel<Message>(64)

    val handleDeviceFound = suspend { device: BleHubDevice ->
      val now = System.currentTimeMillis()
      if (device.id !in devices) send(Found(device))
      devices[device.id] = SeenDevice(device, now)
    }

    val handleCleanUp = suspend {
      val cutoff = System.currentTimeMillis() - deviceTimeout.inWholeMilliseconds
      val lost = devices.values.filter { it.lastSeen < cutoff }.map { it.device }

      lost.forEach { device ->
        devices.remove(device.id)
        send(Lost(device))
      }
    }

    val listener = object : BleDiscovererListener {
      override fun onDeviceDiscovered(deviceId: Long) {
        channel.trySend(Message.DeviceFound(BleHubDevice(deviceId)))
      }

      override fun onDiscoveryFailed(code: Int, reason: String) {
        channel.close(DiscoveryException("BLE discovery failed (code=$code): $reason"))
      }

      override fun onDiscoveryStopped() {
        channel.close(DiscoveryException("BLE discovery stopped unexpectedly"))
      }

      override fun onDiscoveryStarted() {}
    }

    val nativeDiscoverer = NativeBleDiscoverer(serviceUuid, listener)

    val cleaningJob = launch {
      while (true) {
        channel.send(Message.CleanUp)
        delay(1000.milliseconds)
      }
    }

    val processingJob = launch {
      for (message in channel) {
        when (message) {
          is Message.DeviceFound -> handleDeviceFound(message.device)
          is Message.CleanUp -> handleCleanUp()
        }
      }
    }

    nativeDiscoverer.start()

    awaitClose {
      cleaningJob.cancel()
      processingJob.cancel()
      nativeDiscoverer.close()
    }
  }

  private sealed interface Message {
    data class DeviceFound(val device: BleHubDevice) : Message
    data object CleanUp : Message
  }

  private data class SeenDevice(
    val device: BleHubDevice,
    val lastSeen: Long
  )
}
