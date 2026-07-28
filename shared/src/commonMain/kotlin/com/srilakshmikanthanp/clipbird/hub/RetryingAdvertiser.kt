package com.srilakshmikanthanp.clipbird.hub

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

class RetryingAdvertiser(
  private val delegate: Advertiser
) : Advertiser {
  override suspend fun advertise() {
    while (currentCoroutineContext().isActive) {
      try {
        delegate.advertise()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Error in BLE advertising: ${e.message}", e, TAG)
        delay(RETRY_DELAY)
      }
    }
  }

  companion object {
    const val TAG = "RetryingAdvertiser"
    val RETRY_DELAY = 5.seconds
  }
}
