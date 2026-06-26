package com.srilakshmikanthanp.clipbird.ffi

import com.srilakshmikanthanp.clipbird.ffi.NativeClipbirdLoader.library
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

internal object NativeFfiError {
  private val lastErrorMessageHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_error_last_error_message"),
      FunctionDescriptor.of(ValueLayout.ADDRESS),
    )
  }

  private val lastErrorCodeHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_error_last_error_code"),
      FunctionDescriptor.of(ValueLayout.JAVA_INT),
    )
  }

  fun lastErrorMessage(): String {
    val ptr = lastErrorMessageHandle.invoke() as MemorySegment
    if (ptr == MemorySegment.NULL) throw IllegalStateException("Failed to get last error message")
    return ptr.getString(0)
  }

  fun lastErrorCode(): Int {
    return lastErrorCodeHandle.invoke() as Int
  }

  fun lastError(): String {
    return "Error code: ${lastErrorCode()}, message: ${lastErrorMessage()}"
  }
}
