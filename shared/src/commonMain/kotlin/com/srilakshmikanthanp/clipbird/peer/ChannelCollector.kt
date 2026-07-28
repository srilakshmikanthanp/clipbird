package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdProtocolServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

open class ChannelCollector<P: PairedDevice>(
  private val protocolServer: ClipbirdProtocolServer<P>,
  private val protocolClient: ClipbirdProtocolClient<P>,
  private val peerHub: PeerHub<P>,
) {
  private suspend fun collectServerChannels() {
    protocolServer.devices.collect { device ->
      peerHub.consume(device)
    }
  }

  private suspend fun collectClientChannels() {
    protocolClient.devices.collect { device ->
      peerHub.consume(device)
    }
  }

  suspend fun run() = coroutineScope {
    launch { collectClientChannels() }
    launch { collectServerChannels() }
    launch { protocolServer.run() }
    launch { protocolClient.run() }
  }
}
