package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class NoncePacket(
  val nonce: ByteArray,
) : Packet

fun Packet.asNoncePacket(): NoncePacket {
  if (this !is NoncePacket) throw PacketMismatchException(NoncePacket::class, this)
  return this
}
