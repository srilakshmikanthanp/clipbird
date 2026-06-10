package com.srilakshmikanthanp.clipbird.packet

class UnknownPacketTypeException (
  packetType: Int,
  message: String? = null,
  cause: Throwable? = null
) : Exception(message, cause)
