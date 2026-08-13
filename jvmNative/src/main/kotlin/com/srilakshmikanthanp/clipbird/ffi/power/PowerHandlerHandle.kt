package com.srilakshmikanthanp.clipbird.ffi.power

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.bindings.clipbird_power_handler_callback_t
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

object PowerHandlerHandle {
  fun create(arena: Arena, onSleep: () -> Unit, onWake: () -> Unit): MemorySegment {
    val sleepCallback = clipbird_power_handler_callback_t.allocate({ _ -> onSleep() }, arena)
    val wakeCallback = clipbird_power_handler_callback_t.allocate({ _ -> onWake() }, arena)
    return Clipbird.clipbird_power_handler_create(sleepCallback, wakeCallback, MemorySegment.NULL).orThrow {
      IOException("Failed to create power handler: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(handler: MemorySegment) {
    Clipbird.clipbird_power_handler_destroy(handler)
  }
}
