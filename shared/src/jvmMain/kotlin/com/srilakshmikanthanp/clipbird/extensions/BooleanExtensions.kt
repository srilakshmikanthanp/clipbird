package com.srilakshmikanthanp.clipbird.extensions

fun Boolean.orThrow(supplier: () -> Throwable): Boolean {
  if (!this) {
    throw supplier()
  } else {
    return this
  }
}
