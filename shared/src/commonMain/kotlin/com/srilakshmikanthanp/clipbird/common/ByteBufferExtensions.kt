package com.srilakshmikanthanp.clipbird.common

import java.nio.ByteBuffer

fun ByteBuffer.followedBy(more: ByteBuffer): ByteBuffer {
  val merged = ByteBuffer.allocate(remaining() + more.remaining())
  merged.put(this)
  merged.put(more)
  merged.flip()
  return merged
}
