package com.srilakshmikanthanp.clipbird.io

import co.touchlab.kermit.Logger

fun Channel.closeQuietly() {
  try {
    close()
  } catch (e: Exception) {
    Logger.w("Channel close exception: ${e.message}", e)
  }
}
