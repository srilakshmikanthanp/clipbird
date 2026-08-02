package com.srilakshmikanthanp.clipbird.peer.server

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerConnection
import com.srilakshmikanthanp.clipbird.peer.PeerHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

open class ClipbirdProtocolServer<P: PairedDevice>(
  private val advertiser: Advertiser,
  private val server: ClipbirdServer,
  private val handshakeProtocol: ClipbirdServerHandshakeProtocol<P>,
  private val peerHub: PeerHub<P>,
  private val scope: CoroutineScope,
) {
  private var job: Job? = null
  private suspend fun handleChannel(channel: Channel) {
    try {
      val peerConnection = handshakeProtocol.handshake(channel)
      peerHub.consume(peerConnection)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.e("Handshake failed: ${e.message}", e, TAG)
      channel.closeQuietly()
    }
  }

  private suspend fun doAdvertise() = coroutineScope {
    advertiser.advertise()
  }

  private suspend fun doServe() = coroutineScope {
    server.channels.collect { channel -> this@coroutineScope.launch { handleChannel(channel) } }
  }

  private suspend fun run() = coroutineScope {
    launch { doAdvertise() }
    launch { doServe() }
  }

  fun start() {
    if (job?.isActive == true) return
    job = scope.launch { run() }
  }

  fun stop() {
    job?.cancel()
  }

  companion object {
    const val TAG = "ClipbirdProtocolServer"
  }
}
