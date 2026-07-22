package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.io.Channel
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PacketDeduplicator(private val ttlInMillis: Long = 5 * 60 * 1000L) : PacketInterceptor {
  private val seen = ConcurrentHashMap<Uuid, Long>()

  override suspend fun intercept(channel: Channel, packet: Packet): Packet? {
    if (packet !is RoutedPacket) return packet
    val now = System.currentTimeMillis()
    seen.entries.removeIf { it.value < now - ttlInMillis }
    return if (seen.putIfAbsent(packet.id, now) == null) packet else null
  }
}
