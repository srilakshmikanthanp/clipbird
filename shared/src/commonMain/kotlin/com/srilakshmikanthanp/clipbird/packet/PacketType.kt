package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable(with = PacketTypeSerializer::class)
enum class PacketType(val value: Int) {
  PairingPacket(0x01);

  companion object {
    fun from(value: Int): PacketType {
      return entries.find { it.value == value } ?: throw UnknownPacketTypeException(value)
    }
  }
}
