package com.srilakshmikanthanp.clipbird.pairing

import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import co.touchlab.kermit.Logger
import kotlin.time.Duration.Companion.seconds

class RetryingPairingServer<C : Channel>(
  private val delegate: PairingServer<C>
) : PairingServer<C> {
  override val channels: Flow<C> = flow {
    while (currentCoroutineContext().isActive) {
      try {
        emitAll(delegate.channels)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Pairing server failed", e)
        delay(5.seconds)
      }
    }
  }
}
