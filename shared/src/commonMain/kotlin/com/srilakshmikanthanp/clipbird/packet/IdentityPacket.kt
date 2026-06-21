package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class IdentityPacket(
  val deviceId: Long
) : Packet
