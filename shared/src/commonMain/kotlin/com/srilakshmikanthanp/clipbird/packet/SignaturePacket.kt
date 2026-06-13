package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class SignaturePacket(
  val signature: ByteArray,
) : Packet

fun Packet.asSignaturePacket(): SignaturePacket {
  if (this !is SignaturePacket) throw PacketMismatchException(SignaturePacket::class, this)
  return this
}
