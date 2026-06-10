package com.srilakshmikanthanp.clipbird.io

interface Server : AutoCloseable {
  /**
   * Accepts the next inbound connection.
   *
   * @throws ServerClosedException when server is closed while waiting for a connection.
   * @throws ChannelConnectionException when accept/handshake fails.
   */
  @Throws(ServerClosedException::class, ChannelConnectionException::class)
  suspend fun accept(): Channel

  /**
   * Stops listening.
   */
  override fun close()
}
