package com.srilakshmikanthanp.clipbird.io

interface ClientFactory<T : ServerEndpoint> {
  /**
   * Opens a new channel to [endpoint].
   *
   * @throws ChannelConnectionException when connection/handshake fails.
   */
  @Throws(ChannelConnectionException::class)
  suspend fun connect(endpoint: T): Channel
}
