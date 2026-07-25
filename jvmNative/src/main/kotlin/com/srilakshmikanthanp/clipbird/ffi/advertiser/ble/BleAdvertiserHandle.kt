package com.srilakshmikanthanp.clipbird.ffi.advertiser.ble

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_advertiser_ble_listener
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_advertising_failed_t
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_advertising_started_t
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_on_advertising_stopped_t
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object BleAdvertiserHandle {
  @OptIn(ExperimentalUuidApi::class)
  fun create(arena: Arena, serviceUuid: Uuid, serviceData: ByteArray, listener: BleAdvertiserListener): MemorySegment {
    val startedCallback = clipbird_on_advertising_started_t.allocate(
      { context ->
        listener.onAdvertisingStarted()
      },
      arena
    )

    val failedCallback = clipbird_on_advertising_failed_t.allocate(
      { code, reason, _ ->
        listener.onAdvertisingFailed(code, reason.reinterpret(Long.MAX_VALUE).getString(0))
      },
      arena
    )

    val stoppedCallback = clipbird_on_advertising_stopped_t.allocate(
      { context -> listener.onAdvertisingStopped() },
      arena
    )

    val structure = clipbird_advertiser_ble_listener.allocate(arena)

    clipbird_advertiser_ble_listener.on_started(structure, startedCallback)
    clipbird_advertiser_ble_listener.on_failed(structure, failedCallback)
    clipbird_advertiser_ble_listener.on_stopped(structure, stoppedCallback)
    clipbird_advertiser_ble_listener.context(structure, MemorySegment.NULL)

    return Arena.ofConfined().use { confArena ->
      Clipbird.clipbird_advertiser_ble_create(
        confArena.allocateFrom(serviceUuid.toString()),
        confArena.allocateFrom(ValueLayout.JAVA_BYTE, *serviceData),
        serviceData.size,
        structure
      )
    }.orThrow {
      IOException("Failed to create BLE advertiser: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun start(advertiser: MemorySegment) {
    Clipbird.clipbird_advertiser_start(advertiser).orThrow {
      IOException("Failed to start advertising: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun stop(advertiser: MemorySegment) {
    Clipbird.clipbird_advertiser_stop(advertiser).orThrow {
      IOException("Failed to stop advertising: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(advertiser: MemorySegment) {
    Clipbird.clipbird_advertiser_destroy(advertiser)
  }
}
