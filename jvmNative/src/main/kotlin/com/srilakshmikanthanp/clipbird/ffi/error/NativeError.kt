package com.srilakshmikanthanp.clipbird.ffi.error

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import java.lang.foreign.MemorySegment

object NativeError {
  fun lastErrorMessage(): String {
    val ptr = Clipbird.clipbird_error_last_error_message()
    if (ptr == MemorySegment.NULL) throw IllegalStateException("Failed to get last error message")
    return ptr.getString(0)
  }

  fun lastErrorCode(): Int {
    return Clipbird.clipbird_error_last_error_code()
  }
}