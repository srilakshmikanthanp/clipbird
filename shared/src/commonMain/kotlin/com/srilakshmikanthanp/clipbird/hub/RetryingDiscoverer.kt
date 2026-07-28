package com.srilakshmikanthanp.clipbird.hub

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

class RetryingDiscoverer<T : HubDevice>(
  private val delegate: Discoverer<T>
) : Discoverer<T> {
  override val events: Flow<DiscoveryEvent<T>> = flow {
    while (currentCoroutineContext().isActive) {
      try {
        emitAll(delegate.events)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("BLE discovery failed: ${e.message}", e, TAG)
        delay(RETRY_DELAY)
      }
    }
  }

  companion object {
    const val TAG = "RetryingDiscoverer"
    val RETRY_DELAY = 5.seconds
  }
}
