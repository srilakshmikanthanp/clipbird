package com.srilakshmikanthanp.clipbird.io.bluetooth.ffi

import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.NativeClipbirdLoader.library
import com.srilakshmikanthanp.clipbird.ffi.NativeFfiError
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

private object BluetoothServerFfiBindings {
  private val acceptHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_server_accept"),
      FunctionDescriptor.of(
        ValueLayout.ADDRESS,
        ValueLayout.ADDRESS, // server*
      ),
    )
  }

  fun accept(server: MemorySegment): MemorySegment {
    val result = acceptHandle.invoke(server) as MemorySegment
    if (result == MemorySegment.NULL) {
      throw IOException("Failed to accept connection: ${NativeFfiError.lastErrorMessage()}")
    }
    return result
  }

  private val destroyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_server_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    )
  }

  fun destroy(server: MemorySegment) {
    destroyHandle.invoke(server)
  }
}

class BluetoothServerFfi(private val server: MemorySegment) : Server {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothServerFfiBindings.destroy(server)
  }

  override suspend fun accept(): Channel = withContext(Dispatchers.IO) {
    BluetoothChannelFfi(BluetoothServerFfiBindings.accept(server))
  }

  override fun close() {
    cleanable.clean()
  }
}
