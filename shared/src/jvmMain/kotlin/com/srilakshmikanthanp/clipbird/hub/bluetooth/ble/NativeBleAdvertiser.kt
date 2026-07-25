package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.advertiser.ble.BleAdvertiserHandle
import com.srilakshmikanthanp.clipbird.ffi.advertiser.ble.BleAdvertiserListener
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import java.lang.foreign.Arena
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class NativeBleAdvertiser(serviceUuid: Uuid, serviceData: ByteArray, listener: BleAdvertiserListener) : AutoCloseable {
  private val arena = Arena.ofShared()

  private val advertiser = runCatching { BleAdvertiserHandle.create(arena, serviceUuid, serviceData, listener) }.getOrElse {
    throw AdvertisingException("Failed to create BLE advertiser: ${it.message}", it)
  }

  private val cleanable = NativeCleaners.cleaner.register(this) {
    BleAdvertiserHandle.destroy(advertiser)
    arena.close()
  }

  fun start() {
    runCatching { BleAdvertiserHandle.start(advertiser) }.getOrElse{ throw AdvertisingException("Failed to start advertising: ${it.message}", it) }
  }

  fun stop() {
    runCatching { BleAdvertiserHandle.stop(advertiser) }.getOrElse{ throw AdvertisingException("Failed to stop advertising: ${it.message}", it) }
  }

  override fun close() {
    cleanable.clean()
  }
}
