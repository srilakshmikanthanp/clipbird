package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class NoncePacket(
  val nonce: ByteArray,
) : Packet
