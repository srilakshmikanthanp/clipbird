package com.srilakshmikanthanp.clipbird.io

import java.io.EOFException
import java.io.IOException

interface Channel : AutoCloseable {
  /**
   * Read Exactly [size] bytes from the underlying transport, suspending until enough data is available.
   *
   * @param size the exact number of bytes to read, must be positive
   * @throws EOFException if the channel is closed before reading the requested number of bytes
   * @throws IOException for failures during read
   */
  @Throws(EOFException::class, IOException::class)
  suspend fun readExactly(size: Int = 1): ByteArray

  /**
   * Writes all bytes in [data] to the underlying transport.
   *
   * @throws IOException for failures during write
   */
  @Throws(IOException::class)
  suspend fun write(data: ByteArray, offset: Int = 0, length: Int = data.size - offset)

  /**
   * Closes the channel and unblocks pending operations.
   *
   * Calling `close` multiple times should be safe.
   */
  override fun close()
}
