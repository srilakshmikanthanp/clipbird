package com.srilakshmikanthanp.clipbird.peer.authentication

import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.peer.PeerException

class AuthenticationException(
  message: String,
  cause: Throwable? = null
) : PeerException(
  errorCode = ErrorCode.AUTHENTICATION_FAILED,
  message = message,
  cause = cause
)
