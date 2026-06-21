package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class PairingPacket (
  val deviceId: Long,
  val deviceName: String,
  val publicKey: ByteArray
) : Packet
