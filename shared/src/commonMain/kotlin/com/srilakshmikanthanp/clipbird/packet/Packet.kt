package com.srilakshmikanthanp.clipbird.packet

sealed interface Packet

fun Packet.getType(): PacketType {
  return when (this) {
    is PairingPacket -> PacketType.PairingPacketType
    is NoncePacket -> PacketType.NoncePacketType
    is SignaturePacket -> PacketType.SignaturePacketType
  }
}
