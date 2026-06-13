package com.srilakshmikanthanp.clipbird.packet

import kotlin.reflect.KClass

open class PacketException(
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)

class PacketMismatchException(
  expectedPacket: KClass<out Packet>,
  actualPacket: Packet,
  message: String = "Expected packet of type ${expectedPacket.simpleName}, but received ${actualPacket::class.simpleName}",
  cause: Throwable? = null
) : PacketException(message, cause)
