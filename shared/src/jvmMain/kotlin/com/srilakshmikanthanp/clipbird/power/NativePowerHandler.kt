package com.srilakshmikanthanp.clipbird.power

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.power.PowerHandlerHandle
import java.lang.foreign.Arena

class NativePowerHandler(onSleep: () -> Unit, onWake: () -> Unit) : AutoCloseable {
  private val arena = Arena.ofShared()
  private val handle = runCatching { PowerHandlerHandle.create(arena, onSleep, onWake) }.getOrElse {
    arena.close()
    throw it
  }

  private val cleanable = NativeCleaners.cleaner.register(this) {
    PowerHandlerHandle.destroy(handle)
    arena.close()
  }

  override fun close() {
    cleanable.clean()
  }
}
