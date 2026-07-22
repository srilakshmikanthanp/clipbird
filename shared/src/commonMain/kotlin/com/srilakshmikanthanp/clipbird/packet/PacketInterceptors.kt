package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.io.Channel

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
