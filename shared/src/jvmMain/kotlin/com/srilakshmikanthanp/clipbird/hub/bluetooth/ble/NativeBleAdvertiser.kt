package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private object BleAdvertiserHandle {
  @OptIn(ExperimentalUuidApi::class)
  fun create(serviceUuid: Uuid, serviceData: ByteArray): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_advertiser_ble_create(
        arena.allocateFrom(serviceUuid.toString()),
        arena.allocateFrom(ValueLayout.JAVA_BYTE, *serviceData),
        serviceData.size
      )
    }.orThrow {
      AdvertisingException("Failed to create BLE advertiser: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun start(advertiser: MemorySegment) {
    Clipbird.clipbird_advertiser_start(advertiser).orThrow {
      AdvertisingException("Failed to start advertising: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun stop(advertiser: MemorySegment) {
    Clipbird.clipbird_advertiser_stop(advertiser).orThrow {
      AdvertisingException("Failed to stop advertising: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(advertiser: MemorySegment) {
    Clipbird.clipbird_advertiser_destroy(advertiser)
  }
}

@OptIn(ExperimentalUuidApi::class)
class NativeBleAdvertiser(serviceUuid: Uuid, serviceData: ByteArray) : AutoCloseable {
  private val advertiser = BleAdvertiserHandle.create(serviceUuid, serviceData)
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BleAdvertiserHandle.destroy(advertiser)
  }

  fun start() {
    BleAdvertiserHandle.start(advertiser)
  }

  fun stop() {
    BleAdvertiserHandle.stop(advertiser)
  }

  override fun close() {
    cleanable.clean()
  }
}
