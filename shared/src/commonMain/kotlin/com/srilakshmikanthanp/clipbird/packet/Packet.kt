package com.srilakshmikanthanp.clipbird.packet

sealed interface Packet

fun Packet.getType(): PacketType {
  return when (this) {
    is PairingPacket -> PacketType.PairingPacket
  }
}
