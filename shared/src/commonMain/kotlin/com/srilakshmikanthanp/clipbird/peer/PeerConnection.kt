package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice

open class PeerConnection(
  val device: PairedDevice,
  val channel: Channel
) : AutoCloseable {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is PeerConnection) return false
    if (device != other.device) return false
    return true
  }

  override fun hashCode(): Int {
    return device.hashCode()
  }

  override fun close() {
    channel.close()
  }
}
