package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.pairing.PairedDevice

fun interface ChannelConnectionChecker {
  fun isConnected(device: PairedDevice): Boolean
}
