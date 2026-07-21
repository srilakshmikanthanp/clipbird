package com.srilakshmikanthanp.clipbird.ffi.log

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

object NativeLogBridge {
  private val dispatcher = MethodHandles.lookup().findStatic(
    NativeLogBridge::class.java,
    "dispatch",
    MethodType.methodType(Void.TYPE, Int::class.javaPrimitiveType, MemorySegment::class.java, MemorySegment::class.java)
  )

  private val descriptor = FunctionDescriptor.ofVoid(
    ValueLayout.JAVA_INT,
    ValueLayout.ADDRESS,
    ValueLayout.ADDRESS
  )

  private val arena = Arena.ofShared()

  private val stub: MemorySegment = Linker.nativeLinker().upcallStub(
    dispatcher,
    descriptor,
    arena
  )

  @Volatile
  private var callback: ((Int, String) -> Unit)? = null

  fun setCallback(callback: (Int, String) -> Unit) {
    this.callback = callback
    Clipbird.clipbird_log_set_callback(stub, MemorySegment.NULL)
  }

  fun clearCallback() {
    Clipbird.clipbird_log_clear_callback()
    callback = null
  }

  @Suppress("unused")
  @JvmStatic
  fun dispatch(level: Int, message: MemorySegment, context: MemorySegment) {
    callback?.invoke(level, message.reinterpret(Long.MAX_VALUE).getString(0))
  }
}
