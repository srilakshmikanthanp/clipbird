package com.srilakshmikanthanp.clipbird.pairing

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.use

open class PairingChannelCollector<PC: PairingCandidate, P: PairedDevice, C: Channel>(
  private val pairingServer : PairingServer<C>,
  private val service: PairingService<PC, P, C>,
  private val scope: CoroutineScope,
) {
  private var job: Job? = null

  private suspend fun doCollect() {
    pairingServer.channels.collect { channel ->
      try {
        channel.use { service.pair(it) }
      } catch (e: Exception) {
        Logger.e("Failed to pair channel", e)
      }
    }
  }

  fun start() {
    if (job?.isActive == true) return
    job = scope.launch { doCollect() }
  }

  fun stop() {
    job?.cancel()
  }
}
