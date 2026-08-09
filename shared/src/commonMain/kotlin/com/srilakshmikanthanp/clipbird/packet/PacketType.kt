package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable(with = PacketTypeSerializer::class)
enum class PacketType(val value: Int) {
  PairingPacketType(0x01),
  ClipboardDataPacketType(0x05),
  ErrorPacketType(0xFF);

  companion object {
    fun from(value: Int): PacketType {
      return entries.find { it.value == value } ?: throw UnknownPacketException(value)
    }
  }
}
