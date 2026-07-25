package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.discoverer.ble.BleDiscovererHandle
import com.srilakshmikanthanp.clipbird.ffi.discoverer.ble.BleDiscovererListener
import com.srilakshmikanthanp.clipbird.hub.DiscoveryException
import java.lang.foreign.Arena
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class NativeBleDiscoverer(serviceUuid: Uuid, listener: BleDiscovererListener) : AutoCloseable {
  private val arena = Arena.ofShared()

  private val discoverer = runCatching { BleDiscovererHandle.create(arena, serviceUuid, listener) }.getOrElse {
    arena.close()
    throw DiscoveryException("Failed to create BLE discoverer: ${it.message}", it)
  }

  private val cleanable = NativeCleaners.cleaner.register(this) {
    BleDiscovererHandle.destroy(discoverer)
    arena.close()
  }

  fun start() {
    runCatching { BleDiscovererHandle.start(discoverer) }.getOrElse {
      throw DiscoveryException("Failed to start BLE discovery: ${it.message}", it)
    }
  }

  fun stop() {
    runCatching { BleDiscovererHandle.stop(discoverer) }.getOrElse {
      throw DiscoveryException("Failed to stop BLE discovery: ${it.message}", it)
    }
  }

  override fun close() {
    cleanable.clean()
  }
}
