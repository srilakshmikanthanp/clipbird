package com.srilakshmikanthanp.clipbird.ffi.extensions

fun Boolean.orThrow(supplier: () -> Throwable): Boolean {
  if (!this) {
    throw supplier()
  } else {
    return true
  }
}
