package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice

class ChannelHubConnectionChecker(
  private val channelHub: ChannelHub
) : ChannelConnectionChecker {
  override fun isConnected(device: PairedDevice): Boolean {
    return channelHub.devices.value.any { it.device.id == device.id }
  }
}
