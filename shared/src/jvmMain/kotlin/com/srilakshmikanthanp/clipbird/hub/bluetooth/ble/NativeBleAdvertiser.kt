package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.advertiser.ble.BleAdvertiserHandle
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class NativeBleAdvertiser(serviceUuid: Uuid, serviceData: ByteArray) : AutoCloseable {
  private val advertiser = runCatching { BleAdvertiserHandle.create(serviceUuid, serviceData) }.getOrElse {
    throw AdvertisingException("Failed to create BLE advertiser: ${it.message}", it)
  }

  private val cleanable = NativeCleaners.cleaner.register(this) {
    BleAdvertiserHandle.destroy(advertiser)
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
