package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable(with = PacketTypeSerializer::class)
enum class PacketType(val value: Int) {
  PairingPacketType(0x01),
  NoncePacketType(0x02),
  SignaturePacketType(0x03);

  companion object {
    fun from(value: Int): PacketType {
      return entries.find { it.value == value } ?: throw UnknownPacketTypeException(value)
    }
  }
}
