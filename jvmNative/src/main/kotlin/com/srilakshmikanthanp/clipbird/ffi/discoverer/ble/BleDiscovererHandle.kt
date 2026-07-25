package com.srilakshmikanthanp.clipbird.ffi.discoverer.ble

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_discoverer_ble_listener
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_device_discovered_t
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_discovery_failed_t
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_discovery_started_t
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_discovery_stopped_t
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object BleDiscovererHandle {
  @OptIn(ExperimentalUuidApi::class)
  fun create(arena: Arena, serviceUuid: Uuid, listener: BleDiscovererListener): MemorySegment {
    val startedCallback = clipbird_on_discovery_started_t.allocate(
      { _ -> listener.onDiscoveryStarted() },
      arena
    )

    val discoveredCallback = clipbird_on_device_discovered_t.allocate(
      { deviceId, _ -> listener.onDeviceDiscovered(deviceId) },
      arena
    )

    val failedCallback = clipbird_on_discovery_failed_t.allocate(
      { code, reason, _ -> listener.onDiscoveryFailed(code, reason.reinterpret(Long.MAX_VALUE).getString(0)) },
      arena
    )

    val stoppedCallback = clipbird_on_discovery_stopped_t.allocate(
      { _ -> listener.onDiscoveryStopped() },
      arena
    )

    val structure = clipbird_discoverer_ble_listener.allocate(arena)

    clipbird_discoverer_ble_listener.on_started(structure, startedCallback)
    clipbird_discoverer_ble_listener.on_device_discovered(structure, discoveredCallback)
    clipbird_discoverer_ble_listener.on_failed(structure, failedCallback)
    clipbird_discoverer_ble_listener.on_stopped(structure, stoppedCallback)
    clipbird_discoverer_ble_listener.context(structure, MemorySegment.NULL)

    return Arena.ofConfined().use { confArena ->
      Clipbird.clipbird_discoverer_ble_create(
        confArena.allocateFrom(serviceUuid.toString()),
        structure
      )
    }.orThrow {
      IOException("Failed to create BLE discoverer: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun start(discoverer: MemorySegment) {
    Clipbird.clipbird_discoverer_start(discoverer).orThrow {
      IOException("Failed to start discovery: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun stop(discoverer: MemorySegment) {
    Clipbird.clipbird_discoverer_stop(discoverer).orThrow {
      IOException("Failed to stop discovery: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(discoverer: MemorySegment) {
    Clipbird.clipbird_discoverer_destroy(discoverer)
  }
}
