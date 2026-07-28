package com.srilakshmikanthanp.clipbird.peer.server

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

class RetryingClipbirdServer(
  private val delegate: ClipbirdServer
) : ClipbirdServer {
  override val channels: Flow<Channel> = flow {
    while (currentCoroutineContext().isActive) {
      try {
        emitAll(delegate.channels)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Error collecting server channels: ${e.message}", e, TAG)
        delay(RETRY_DELAY)
      }
    }
  }

  companion object {
    const val TAG = "RetryingClipbirdServer"
    val RETRY_DELAY = 5.seconds
  }
}
