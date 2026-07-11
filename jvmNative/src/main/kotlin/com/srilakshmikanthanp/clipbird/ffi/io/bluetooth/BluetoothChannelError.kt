package com.srilakshmikanthanp.clipbird.ffi.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import java.io.IOException

internal fun Int.toChannelReadExactlyException(): IOException {
  return when (this) {
    Clipbird.CLIPBIRD_IO_CHANNEL_EOF() -> IOException("Reached end of channel: ${NativeErrorHandle.lastErrorMessage()}")
    else -> IOException("Failed to read from channel: ${NativeErrorHandle.lastErrorMessage()}")
  }
}
