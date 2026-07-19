package com.srilakshmikanthanp.clipbird.authentication

class AuthenticationException(
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)
