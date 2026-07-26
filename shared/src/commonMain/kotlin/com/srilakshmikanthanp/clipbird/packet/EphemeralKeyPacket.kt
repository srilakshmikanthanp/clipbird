package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class EphemeralKeyPacket(
  val publicKey: ByteArray
) : Packet
