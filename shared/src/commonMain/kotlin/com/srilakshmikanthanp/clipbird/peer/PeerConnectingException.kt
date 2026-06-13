package com.srilakshmikanthanp.clipbird.peer

open class PeerConnectingException(
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)

class SignatureVerificationException(
  message: String,
  cause: Throwable? = null
) : PeerConnectingException(message, cause)
