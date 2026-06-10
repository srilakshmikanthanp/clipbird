package com.srilakshmikanthanp.clipbird.io

import kotlinx.coroutines.flow.StateFlow

interface Channel : AutoCloseable {
  /**
   * Indicates whether the channel is currently open.
   * Should emit `false` when the channel is closed.
   */
  val isOpen: StateFlow<Boolean>

  /**
   * Read Exactly [size] bytes from the underlying transport, suspending until enough data is available.
   *
   * @throws ChannelClosedException when the channel closes before enough bytes are available.
   * @throws ChannelReadException for failures during read
   */
  @Throws(ChannelClosedException::class, ChannelReadException::class)
  suspend fun readExactly(size: Int = 1): ByteArray

  /**
   * Writes all bytes in [data] to the underlying transport.
   *
   * @throws ChannelClosedException when the channel is already closed.
   * @throws ChannelWriteException for non-timeout write failures.
   */
  @Throws(ChannelClosedException::class, ChannelWriteException::class)
  suspend fun write(data: ByteArray)

  /**
   * Closes the channel and unblocks pending operations.
   *
   * Calling `close` multiple times should be safe.
   */
  override fun close()
}
