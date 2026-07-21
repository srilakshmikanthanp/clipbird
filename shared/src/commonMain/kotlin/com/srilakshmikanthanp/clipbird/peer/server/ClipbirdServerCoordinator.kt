package com.srilakshmikanthanp.clipbird.peer.server

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ClipbirdServerCoordinator(
  private val advertiser: Advertiser,
  private val server: ClipbirdServer,
  private val scope: CoroutineScope,
) {
  private val advertiserJobDelegate = lazy { scope.launch { doAdvertise() } }
  private val serverJobDelegate = lazy { scope.launch { doServe() } }

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
    advertiserJobDelegate.value
    serverJobDelegate.value
  }

  fun stop() {
    if (advertiserJobDelegate.isInitialized()) advertiserJobDelegate.value.cancel()
    if (serverJobDelegate.isInitialized()) serverJobDelegate.value.cancel()
  }

  companion object {
    const val TAG = "ClipbirdServerCoordinator"
  }
}
