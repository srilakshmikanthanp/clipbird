package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.ffi.advertiser.ble.BleAdvertiserListener
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.nio.ByteBuffer
import kotlin.coroutines.resumeWithException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual class BleAdvertiser(
  private val serviceUuid: Uuid,
  private val hostDeviceProvider: HostDeviceProvider,
) : Advertiser {
  actual override suspend fun advertise(): Unit = coroutineScope {
    val deferred = CompletableDeferred<Nothing>()

    val idBytes = ByteBuffer
      .allocate(8)
      .putLong(hostDeviceProvider.get().id.toLong())
      .array()

    val advertiser = suspendCancellableCoroutine { continuation ->
      lateinit var nativeAdvertiser: NativeBleAdvertiser

      val listener = object : BleAdvertiserListener {
        override fun onAdvertisingStarted() {
          if (continuation.isActive) {
            continuation.resume(nativeAdvertiser)
          }
        }

        override fun onAdvertisingFailed(code: Int, reason: String) {
          if (continuation.isActive) {
            continuation.resumeWithException(AdvertisingException("Failed to start advertising: $reason"))
          }
        }

        override fun onAdvertisingStopped() {
          deferred.completeExceptionally(AdvertisingException("Advertising stopped unexpectedly"))
        }
      }

      nativeAdvertiser = NativeBleAdvertiser(
        serviceUuid,
        idBytes,
        listener
      )

      try {
        nativeAdvertiser.start()
      } catch (e: Exception) {
        if (continuation.isActive) continuation.resumeWithException(e)
      }

      continuation.invokeOnCancellation {
        nativeAdvertiser.close()
      }
    }

    advertiser.use {
      deferred.await()
    }
  }
}
