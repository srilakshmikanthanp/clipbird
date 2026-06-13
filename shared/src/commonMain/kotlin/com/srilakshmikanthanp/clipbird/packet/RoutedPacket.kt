package com.srilakshmikanthanp.clipbird.packet

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed interface RoutedPacket : Packet {
  val id: Uuid
}
