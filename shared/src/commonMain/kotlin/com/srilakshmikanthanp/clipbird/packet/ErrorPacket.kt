package com.srilakshmikanthanp.clipbird.packet

import kotlinx.serialization.Serializable

@Serializable
class ErrorPacket(
  val errorCode: ErrorCode,
  val errorMessage: String
) : Packet {
  enum class ErrorCode {
    AUTHENTICATION_FAILED,
    DEVICE_NOT_PAIRED,
    ALREADY_CONNECTED,
    INVALID_PACKET
  }
}
