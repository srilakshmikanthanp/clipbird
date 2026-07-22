package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ChannelHubConnectionChecker<P: PairedDevice>(
  private val channelHub: ChannelHub<P>
) : ChannelConnectionChecker {
  override fun isConnected(device: PairedDevice): Boolean {
    return channelHub.devices.value.any { it.id == device.id }
  }
}
