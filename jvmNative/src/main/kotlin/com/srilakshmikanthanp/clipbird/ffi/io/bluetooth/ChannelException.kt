package com.srilakshmikanthanp.clipbird.ffi.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeError
import java.io.IOException

fun Int.toChannelReadExactlyException(): IOException {
  return when (this) {
    Clipbird.CLIPBIRD_IO_CHANNEL_EOF() -> IOException("Reached end of channel: ${NativeError.lastErrorMessage()}")
    else -> IOException("Failed to read from channel: ${NativeError.lastErrorMessage()}")
  }
}
