package com.srilakshmikanthanp.clipbird.io

interface ServerFactory<T : ServerConfig> {
  /**
   * Starts listening with [config].
   *
   * @throws ServerStartException when bind/listen setup fails.
   */
  @Throws(ServerStartException::class)
  suspend fun start(config: T): Server
}
