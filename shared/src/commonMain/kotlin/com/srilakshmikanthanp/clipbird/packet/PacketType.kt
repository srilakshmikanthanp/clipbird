package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable(with = PacketTypeSerializer::class)
enum class PacketType(val value: Int) {
  PairingPacketType(0x01),
  IdentityPacketType(0x02),
  NoncePacketType(0x03),
  SignaturePacketType(0x04),
  ClipboardDataPacketType(0x05),
  EphemeralKeyPacketType(0x06),
  ErrorPacketType(0xFF);

  companion object {
    fun from(value: Int): PacketType {
      return entries.find { it.value == value } ?: throw UnknownPacketException(value)
    }
  }
}
