package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PacketTypeSerializer : KSerializer<PacketType> {
  override val descriptor = PrimitiveSerialDescriptor("PacketType", PrimitiveKind.INT)

  override fun serialize(encoder: Encoder, value: PacketType) {
    encoder.encodeInt(value.value)
  }

  override fun deserialize(decoder: Decoder): PacketType {
    return PacketType.from(decoder.decodeInt())
  }
}
