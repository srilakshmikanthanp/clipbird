package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.io.Channel

class PacketReRouter(
  private val channels: () -> List<Channel>,
) : PacketInterceptor {
  override suspend fun intercept(channel: Channel, packet: Packet): Packet {
    if (packet !is RoutedPacket) return packet

    for (other in channels()) {
      if (other !== channel) {
        other.sendPacket(packet)
      }
    }

    return packet
  }
}
