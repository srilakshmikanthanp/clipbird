package com.srilakshmikanthanp.clipbird.packet

import kotlin.reflect.KClass

open class PacketException(
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)

class PacketMismatchException(
  val expectedPacket: KClass<out Packet>,
  val actualPacket: Packet,
  message: String = "Expected packet of type ${expectedPacket.simpleName}, but received ${actualPacket::class.simpleName}",
  cause: Throwable? = null
) : PacketException(message, cause)

class UnknownPacketException (
  packetType: Int,
  message: String = "Unknown packet type: $packetType",
  cause: Throwable? = null
) : PacketException(message, cause)
