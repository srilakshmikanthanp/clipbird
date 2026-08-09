package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

sealed interface Packet

@OptIn(ExperimentalSerializationApi::class)
fun ByteArray.toPacket(packetType: PacketType): Packet {
  return when (packetType) {
    PacketType.ClipboardDataPacketType -> ProtoBuf.decodeFromByteArray<ClipboardSyncingPacket>(this)
    PacketType.PairingPacketType -> ProtoBuf.decodeFromByteArray<PairingPacket>(this)
    PacketType.ErrorPacketType -> ProtoBuf.decodeFromByteArray<ErrorPacket>(this)
  }
}

@OptIn(ExperimentalSerializationApi::class)
fun Packet.toBytes(): ByteArray = when (this) {
  is ClipboardSyncingPacket -> ProtoBuf.encodeToByteArray(this)
  is PairingPacket -> ProtoBuf.encodeToByteArray(this)
  is ErrorPacket -> ProtoBuf.encodeToByteArray(this)
}

fun Packet.getType(): PacketType {
  return when (this) {
    is ClipboardSyncingPacket -> PacketType.ClipboardDataPacketType
    is PairingPacket -> PacketType.PairingPacketType
    is ErrorPacket -> PacketType.ErrorPacketType
  }
}

inline fun <reified T : Packet> Packet.asPacket(): T {
  if (this !is T) {
    throw PacketMismatchException(T::class, this)
  } else {
    return this
  }
}
