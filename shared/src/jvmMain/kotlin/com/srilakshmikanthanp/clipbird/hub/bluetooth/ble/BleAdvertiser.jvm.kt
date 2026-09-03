package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.ffi.advertiser.ble.BleAdvertiserListener
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import kotlinx.coroutines.CompletableDeferred
import java.nio.ByteBuffer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual class BleAdvertiser(
  private val serviceUuid: Uuid,
  private val hostDeviceProvider: HostDeviceProvider,
) : Advertiser {
  actual override suspend fun advertise(): Unit {
    val idBytes = ByteBuffer
      .allocate(8)
      .putLong(hostDeviceProvider.get().id.toLong())
      .array()

    val started = CompletableDeferred<Unit>()
    val stopped = CompletableDeferred<Nothing>()

    val listener = object : BleAdvertiserListener {
      override fun onAdvertisingStarted() {
        started.complete(Unit)
      }

      override fun onAdvertisingFailed(code: Int, reason: String) {
        started.completeExceptionally(AdvertisingException("Failed to start advertising: $reason"))
      }

      override fun onAdvertisingStopped() {
        stopped.completeExceptionally(AdvertisingException("Advertising stopped unexpectedly"))
      }
    }

    NativeBleAdvertiser(serviceUuid, idBytes, listener).use { nativeAdvertiser ->
      nativeAdvertiser.start()
      started.await()
      stopped.await()
    }
  }
}
