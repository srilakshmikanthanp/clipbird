package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class SignaturePacket(
  val signature: ByteArray,
) : Packet

