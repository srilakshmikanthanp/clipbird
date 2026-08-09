package com.srilakshmikanthanp.clipbird.packet.interceptor

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.Packet
import com.srilakshmikanthanp.clipbird.packet.RoutedPacket
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class PacketExpiry(
  private val skewToleranceMillis: Long = 30_000L,
) : PacketInterceptor {
  override suspend fun intercept(channel: Channel, packet: Packet): Packet? {
    if (packet !is RoutedPacket) return packet

    val age = System.currentTimeMillis() - packet.createdAt

    if (age < -skewToleranceMillis) {
      Logger.d("Packet ${packet.id} appears ${-age}ms from the future, keeping it", null, TAG)
      return packet
    }

    if (age <= packet.ttlMillis) return packet

    Logger.i(
      "Dropping packet ${packet.id}: ${age}ms old, time to live ${packet.ttlMillis}ms. " +
        "Repeated drops from one device usually mean its clock is wrong, not that packets are old.",
      null,
      TAG,
    )

    return null
  }

  private companion object {
    const val TAG = "PacketExpiry"
  }
}
