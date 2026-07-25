package com.srilakshmikanthanp.clipbird.packet.interceptor

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.Packet

fun interface PacketInterceptor {
  suspend fun intercept(channel: Channel, packet: Packet): Packet?
}
