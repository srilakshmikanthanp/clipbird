package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.io.Channel

fun interface PacketInterceptor {
  suspend fun intercept(channel: Channel, packet: Packet): Packet?
}
