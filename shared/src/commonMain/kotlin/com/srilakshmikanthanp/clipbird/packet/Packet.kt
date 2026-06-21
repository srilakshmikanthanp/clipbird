package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.packet.PacketType.IdentityPacketType
import com.srilakshmikanthanp.clipbird.packet.PacketType.NoncePacketType
import com.srilakshmikanthanp.clipbird.packet.PacketType.PairingPacketType
import com.srilakshmikanthanp.clipbird.packet.PacketType.SignaturePacketType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

sealed interface Packet

@OptIn(ExperimentalSerializationApi::class)
fun ByteArray.toPacket(packetType: PacketType): Packet {
  return when (packetType) {
    PairingPacketType -> ProtoBuf.decodeFromByteArray<PairingPacket>(this)
    NoncePacketType -> ProtoBuf.decodeFromByteArray<NoncePacket>(this)
    SignaturePacketType -> ProtoBuf.decodeFromByteArray<SignaturePacket>(this)
    IdentityPacketType -> ProtoBuf.decodeFromByteArray<IdentityPacket>(this)
  }
}

fun Packet.getType(): PacketType {
  return when (this) {
    is PairingPacket -> PairingPacketType
    is NoncePacket -> NoncePacketType
    is SignaturePacket -> SignaturePacketType
    is IdentityPacket -> IdentityPacketType
  }
}
