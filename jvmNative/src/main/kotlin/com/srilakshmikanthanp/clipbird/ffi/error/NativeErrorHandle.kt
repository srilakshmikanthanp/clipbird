package com.srilakshmikanthanp.clipbird.ffi.error

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.lang.foreign.MemorySegment

object NativeErrorHandle {
  fun lastErrorMessage(): String {
    return Clipbird.clipbird_error_last_error_message().orThrow { IllegalStateException("Failed to get last error message") }.getString(0)
  }

  fun lastErrorCode(): Int {
    return Clipbird.clipbird_error_last_error_code()
  }
}