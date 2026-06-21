package com.srilakshmikanthanp.clipbird.authentication

import com.srilakshmikanthanp.clipbird.packet.ErrorPacket.ErrorCode
import com.srilakshmikanthanp.clipbird.peer.PeerException

class AuthenticationException(
  message: String,
  cause: Throwable? = null
) : PeerException(ErrorCode.AUTHENTICATION_FAILED, message, cause)
