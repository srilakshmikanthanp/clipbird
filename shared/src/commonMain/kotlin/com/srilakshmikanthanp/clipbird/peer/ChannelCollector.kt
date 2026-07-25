package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerCoordinator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

open class ChannelCollector<P: PairedDevice>(
  private val serverCoordinator: ClipbirdServerCoordinator<P>,
  private val clientCoordinator: ClipbirdClientCoordinator<P>,
  private val channelHub: ChannelHub<P>,
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

  suspend fun run() = coroutineScope {
    launch { collectClientChannels() }
    launch { collectServerChannels() }
    launch { serverCoordinator.run() }
    launch { clientCoordinator.run() }
  }
}
