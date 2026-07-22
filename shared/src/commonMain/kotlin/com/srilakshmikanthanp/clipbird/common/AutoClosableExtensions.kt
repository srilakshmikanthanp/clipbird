package com.srilakshmikanthanp.clipbird.common

import co.touchlab.kermit.Logger

fun AutoCloseable.closeQuietly() {
  try {
    close()
  } catch (e: Exception) {
    Logger.w("Channel close exception: ${e.message}", e)
  }
}
