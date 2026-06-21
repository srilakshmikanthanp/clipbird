package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class IdentityPacket(
  val deviceId: Long
) : Packet

fun Packet.asIdentityPacket(): IdentityPacket {
  if (this !is IdentityPacket) throw PacketMismatchException(IdentityPacket::class, this)
  return this
}
