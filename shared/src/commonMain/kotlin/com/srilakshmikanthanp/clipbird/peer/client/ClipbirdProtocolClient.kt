package com.srilakshmikanthanp.clipbird.peer.client

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.common.closeQuietly
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.pairing.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerConnection
import com.srilakshmikanthanp.clipbird.peer.PeerHub
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

open class ClipbirdProtocolClient<P : PairedDevice>(
  private val activeDeviceProvider: ActivePairedDeviceProvider<P>,
  private val connector: ClipbirdClientConnector<P>,
  private val decider: ConnectionInitiationDecider,
  private val handshakeProtocol: ClipbirdClientHandshakeProtocol,
  private val peerHub: PeerHub<P>,
  private val scope: CoroutineScope,
) {
  private val jobs = mutableMapOf<ULong, Job>()
  private var job: Job? = null

  private fun CoroutineScope.job(device: P): Job = launch {
    while (isActive) {
      connect(device).also { delay(RETRY_DELAY) }
    }
  }

  private suspend fun connect(device: P) {
    var channel: Channel? = null
    try {
      if (!decider.shouldInitiateConnection(device)) return
      if (peerHub.isConnected(device)) return
      channel = connector.connect(device)
      val peerConnection = handshakeProtocol.handshake(channel, device)
      peerHub.consume(peerConnection)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Logger.e("Connect to ${device.name} failed: ${e.message}", e, TAG)
      channel?.closeQuietly()
    }
  }

  private suspend fun CoroutineScope.doRun() {
    activeDeviceProvider.devices.collect { devices ->
      val current = devices.associateBy { it.id }
      val removed = jobs.keys - current.keys

      removed.forEach { id ->
        Logger.i { "Removing job for device $id" }
        jobs.remove(id)?.cancel()
      }

      current.forEach { (id, device) ->
        if (id !in jobs) {
          jobs[id] = this.job(device)
        }
      }
    }
  }

  private suspend fun run(): Unit = coroutineScope {
    while (isActive) {
      try {
        this.doRun()
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Logger.e("Error in client collector: ${e.message}", e, TAG)
        delay(RETRY_DELAY)
      }
    }
  }

  fun start() {
    if (job?.isActive == true) return
    job = scope.launch { run() }
  }

  fun stop() {
    jobs.values.forEach { it.cancel() }
    jobs.clear()
    job?.cancel()
  }

  companion object {
    const val TAG = "ClipbirdProtocolClient"
    val RETRY_DELAY = 5.seconds
  }
}
