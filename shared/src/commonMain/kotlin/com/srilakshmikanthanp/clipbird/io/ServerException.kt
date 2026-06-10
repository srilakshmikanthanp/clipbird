package com.srilakshmikanthanp.clipbird.io

sealed class ServerException(
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)

class ServerStartException(
  message: String,
  cause: Throwable? = null
) : ServerException(message, cause)

class ServerClosedException(
  message: String,
  cause: Throwable? = null
) : ServerException(message, cause)
