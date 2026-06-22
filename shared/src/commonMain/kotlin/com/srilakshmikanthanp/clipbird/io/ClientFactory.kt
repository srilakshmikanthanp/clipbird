package com.srilakshmikanthanp.clipbird.io

import java.io.IOException

interface ClientFactory<T : ServerEndpoint> {
  /**
   * Opens a new channel to [endpoint].
   *
   * @throws IOException when connection fails.
   * @return a [Channel] representing the connection to the server.
   */
  @Throws(IOException::class)
  suspend fun connect(endpoint: T): Channel
}
