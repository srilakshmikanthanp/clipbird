package com.srilakshmikanthanp.clipbird.io

import java.io.IOException

interface Server : AutoCloseable {
  /**
   * Accepts the next inbound connection.
   *
   * @throws IOException when accepting the connection fails.
   * @return a [Channel] representing the accepted connection.
   */
  @Throws(IOException::class)
  suspend fun accept(): Channel

  /**
   * Stops listening.
   *
   * @throws IOException when closing the server fails.
   */
  @Throws(IOException::class)
  override fun close()
}
