package com.srilakshmikanthanp.clipbird.ffi.advertiser.ble

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
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
  fun create(serviceUuid: Uuid, serviceData: ByteArray): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_advertiser_ble_create(arena.allocateFrom(serviceUuid.toString()), arena.allocateFrom(ValueLayout.JAVA_BYTE, *serviceData), serviceData.size)
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
