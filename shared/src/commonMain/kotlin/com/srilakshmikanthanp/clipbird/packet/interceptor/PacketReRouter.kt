package com.srilakshmikanthanp.clipbird.packet.interceptor

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.Packet
import com.srilakshmikanthanp.clipbird.packet.RoutedPacket
import com.srilakshmikanthanp.clipbird.packet.sendPacket

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
