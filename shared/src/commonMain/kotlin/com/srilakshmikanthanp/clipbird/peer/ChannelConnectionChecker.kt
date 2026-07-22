package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDevice

fun interface ChannelConnectionChecker {
  fun isConnected(device: PairedDevice): Boolean
}
