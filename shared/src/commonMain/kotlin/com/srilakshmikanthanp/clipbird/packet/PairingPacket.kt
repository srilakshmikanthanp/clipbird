package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class PairingPacket (
  val deviceId: Long,
  val deviceName: String,
  val publicKey: ByteArray
) : Packet

fun Packet.asPairingPacket(): PairingPacket {
  if (this !is PairingPacket) throw PacketMismatchException(PairingPacket::class, this)
  return this
}
