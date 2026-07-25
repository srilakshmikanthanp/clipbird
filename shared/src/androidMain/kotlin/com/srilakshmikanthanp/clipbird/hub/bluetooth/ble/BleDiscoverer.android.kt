package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.juul.kable.Advertisement
import com.juul.kable.Scanner
import com.srilakshmikanthanp.clipbird.hub.Discoverer
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.DiscoveryException
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer.Message.CleanUp
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer.Message.DeviceFound
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

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

    val scanner = Scanner {}

    val discoveringJob = launch {
      scanner.advertisements.catch { e ->
        throw DiscoveryException("BLE discovery failed", e)
      }.collect { advertisement ->
        advertisement.toDevice()?.let { device ->
          channel.trySend(DeviceFound(device))
        }
      }
    }

    val cleaningJob = launch {
      while (true) {
        channel.send(CleanUp)
        delay(1000.milliseconds)
      }
    }

    val processingJob = launch {
      for (message in channel) {
        when (message) {
          is DeviceFound -> handleDeviceFound(message.device)
          is CleanUp -> handleCleanUp()
        }
      }
    }

    awaitClose {
      discoveringJob.cancel()
      cleaningJob.cancel()
      processingJob.cancel()
    }
  }

  private fun Advertisement.toDevice(): BleHubDevice? {
    val data = this.manufacturerData(0xFFFF) ?: return null
    val uuid = serviceUuid.toJavaUuid()

    if (data.size != 24) return null

    val buf = ByteBuffer.wrap(data)
    val msb = buf.getLong()
    val lsb = buf.getLong()
    val id = buf.getLong()

    if (msb != uuid.mostSignificantBits || lsb != uuid.leastSignificantBits) return null

    return BleHubDevice(id)
  }

  private data class SeenDevice(val device: BleHubDevice, val lastSeen: Long)

  internal sealed interface Message {
    data class DeviceFound(val device: BleHubDevice) : Message
    data object CleanUp : Message
  }
}
