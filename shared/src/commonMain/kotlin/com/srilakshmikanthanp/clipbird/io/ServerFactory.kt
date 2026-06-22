package com.srilakshmikanthanp.clipbird.io

import java.io.IOException

interface ServerFactory<T : ServerConfig> {
  /**
   * Starts listening with [config].
   *
   * @throws IOException when bind/listen setup fails.
   */
  @Throws(IOException::class)
  suspend fun start(config: T): Server
}
