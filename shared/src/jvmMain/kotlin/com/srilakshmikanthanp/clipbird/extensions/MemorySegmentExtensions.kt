package com.srilakshmikanthanp.clipbird.extensions

import java.lang.foreign.MemorySegment

fun MemorySegment.orThrow(supplier: () -> Throwable): MemorySegment {
  if (this == MemorySegment.NULL) {
    throw supplier()
  } else {
    return this
  }
}
