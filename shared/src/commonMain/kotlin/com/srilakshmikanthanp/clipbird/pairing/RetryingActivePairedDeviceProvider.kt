package com.srilakshmikanthanp.clipbird.pairing

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

class RetryingActivePairedDeviceProvider<P : PairedDevice>(
  private val delegate: ActivePairedDeviceProvider<P>
) : ActivePairedDeviceProvider<P> {
  override val devices: Flow<Collection<P>> = flow {
    while (currentCoroutineContext().isActive) {
      try {
        emitAll(delegate.devices)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Active paired device provider failed: ${e.message}", e, TAG)
        delay(RETRY_DELAY)
      }
    }
  }

  companion object {
    const val TAG = "RetryingActiveDeviceProvider"
    val RETRY_DELAY = 5.seconds
  }
}
