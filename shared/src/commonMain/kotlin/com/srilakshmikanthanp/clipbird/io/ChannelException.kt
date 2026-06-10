package com.srilakshmikanthanp.clipbird.io

sealed class ChannelException(
  message: String,
  cause: Throwable? = null
) : Exception(message, cause)

class ChannelConnectionException(
  message: String,
  cause: Throwable? = null
) : ChannelException(message, cause)

class ChannelClosedException(
  message: String = "Transport is closed",
  cause: Throwable? = null
) : ChannelException(message, cause)

class ChannelReadException(
  message: String,
  cause: Throwable? = null
) : ChannelException(message, cause)

class ChannelWriteException(
  message: String,
  cause: Throwable? = null
) : ChannelException(message, cause)
