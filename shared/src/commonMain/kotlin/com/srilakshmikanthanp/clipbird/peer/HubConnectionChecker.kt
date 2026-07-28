package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.pairing.PairedDevice

class HubConnectionChecker<P: PairedDevice>(
  private val peerHub: PeerHub<P>
) : ChannelConnectionChecker {
  override fun isConnected(device: PairedDevice): Boolean {
    return peerHub.devices.value.any { it.id == device.id }
  }
}
