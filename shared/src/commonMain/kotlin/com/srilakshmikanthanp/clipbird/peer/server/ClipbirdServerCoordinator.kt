package com.srilakshmikanthanp.clipbird.peer.server

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ClipbirdServerCoordinator(
  private val advertiser: Advertiser,
  private val server: ClipbirdServer,
  private val scope: CoroutineScope,
) {
  private var advertiserJob: Job? = null
  private var serverJob: Job? = null

  private suspend fun doAdvertise() {
    try {
      advertiser.advertise()
    } catch (e: Exception) {
      Logger.e("Error in BLE advertising: ${e.message}", e, TAG)
    }
  }

  private suspend fun doServe() {
    try {
      server.channels.collect { }
    } catch (e: Exception) {
      Logger.e("Error collecting server channels: ${e.message}", e, TAG)
    }
  }

  fun start() {
    advertiserJob = scope.launch { doAdvertise() }
    serverJob = scope.launch { doServe() }
  }

  fun stop() {
    advertiserJob?.cancel()
    serverJob?.cancel()
    advertiserJob = null
    serverJob = null
  }

  companion object {
    const val TAG = "ClipbirdServerCoordinator"
  }
}
