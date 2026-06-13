package com.srilakshmikanthanp.clipbird.packet

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.packet.PacketType.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.ByteBuffer

@OptIn(ExperimentalSerializationApi::class)
suspend fun Channel.nextPacket(): Packet {
  val length: Int = ByteBuffer.wrap(readExactly(4)).int
  val type: Int = ByteBuffer.wrap(readExactly(4)).int
  val data: ByteArray = readExactly(length)
  val packetType = PacketType.from(type)
  return when (packetType) {
    PairingPacketType -> ProtoBuf.decodeFromByteArray<PairingPacket>(data)
    NoncePacketType -> ProtoBuf.decodeFromByteArray<NoncePacket>(data)
    SignaturePacketType -> ProtoBuf.decodeFromByteArray<SignaturePacket>(data)
  }
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun Channel.sendPacket(packet: Packet) {
  val encoded = ProtoBuf.encodeToByteArray(packet)
  val length = encoded.size
  val type = packet.getType()
  val buffer = ByteBuffer.allocate(Int.SIZE_BYTES + Int.SIZE_BYTES + length)
    .putInt(length)
    .putInt(type.value)
    .put(encoded)
    .array()
  this.write(buffer)
}
