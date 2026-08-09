package com.srilakshmikanthanp.clipbird.utility

class ByteQueue {
  private var bytes = ByteArray(0)
  private var ptr = 0

  val available: Int get() = bytes.size - ptr

  fun append(more: ByteArray) {
    bytes = if (available == 0) more else bytes.copyOfRange(ptr, bytes.size) + more
    ptr = 0
  }

  fun take(size: Int): ByteArray {
    require(size <= available) { "Asked for $size bytes with only $available buffered" }

    val result = bytes.copyOfRange(ptr, ptr + size)
    ptr += size

    if (ptr == bytes.size) {
      bytes = ByteArray(0)
      ptr = 0
    }

    return result
  }
}
