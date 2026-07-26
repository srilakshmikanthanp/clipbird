package com.srilakshmikanthanp.clipbird.paring

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlin.use

open class PairingChannelCollector<PC: PairingCandidate, P: PairedDevice, C: Channel>(
  private val pairingServer : PairingServer<C>,
  private val service: PairingService<PC, P, C>,
) {
  suspend fun run() {
    pairingServer.channels.collect { channel ->
      try {
        channel.use { service.pair(it) }
      } catch (e: Exception) {
        Logger.e("Failed to pair channel", e)
      }
    }
  }
}
