package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

open class ChannelCollector<P: PairedDevice>(
  private val serverCoordinator: ClipbirdServerCoordinator<P>,
  private val clientCoordinator: ClipbirdClientCoordinator<P>,
  private val channelHub: ChannelHub<P>,
  private val scope: CoroutineScope,
) {
  private suspend fun collectServerChannels() {
    serverCoordinator.devices.collect { device ->
      channelHub.add(device)
    }
  }

  private suspend fun collectClientChannels() {
    clientCoordinator.devices.collect { device ->
      channelHub.add(device)
    }
  }

  private var job: Job? = null

  init {
    scope.launch { collectClientChannels() }
    scope.launch { collectServerChannels() }
  }

  fun start() {
    job = scope.launch {
      coroutineScope {
        launch { serverCoordinator.run() }
        launch { clientCoordinator.run() }
      }
    }
  }

  fun stop() {
    job?.cancel()
  }
}
