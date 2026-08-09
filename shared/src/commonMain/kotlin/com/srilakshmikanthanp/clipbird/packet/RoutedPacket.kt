package com.srilakshmikanthanp.clipbird.packet

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed interface RoutedPacket : Packet {
  val id: Uuid
  val createdAt: Long
  val ttlMillis: Long

  companion object {
    const val DEFAULT_TTL_MILLIS = 60_000L
  }
}
