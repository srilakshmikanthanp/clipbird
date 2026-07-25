package com.srilakshmikanthanp.clipbird.packet.interceptor

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.Packet

class PacketInterceptors(private vararg val interceptors: PacketInterceptor) : PacketInterceptor {
  override suspend fun intercept(channel: Channel,packet: Packet): Packet? {
    var currentPacket: Packet? = packet
    for (interceptor in interceptors) {
      currentPacket = currentPacket?.let { interceptor.intercept(channel, it) }
      if (currentPacket == null) break
    }
    return currentPacket
  }
}
