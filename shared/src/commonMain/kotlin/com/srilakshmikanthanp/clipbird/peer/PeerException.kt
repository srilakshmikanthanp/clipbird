package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.packet.ErrorPacket
import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode

open class PeerException(
  val errorCode: ErrorCode,
  override val message: String,
  cause: Throwable? = null,
) : Exception(message, cause) {
  fun toErrorPacket(): ErrorPacket {
    return ErrorPacket(errorCode = errorCode, errorMessage = message)
  }
}
