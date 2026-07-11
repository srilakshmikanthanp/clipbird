package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.extensions.orThrow
import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.error.NativeError
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.foreign.MemorySegment
import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird

private object BluetoothServerHandle {
  fun accept(server: MemorySegment): MemorySegment {
    return Clipbird.clipbird_io_server_accept(server).orThrow { IOException("Failed to accept connection: ${NativeError.lastErrorMessage()}") }
  }

  fun destroy(server: MemorySegment) {
    Clipbird.clipbird_io_server_destroy(server)
  }
}

class NativeBluetoothServer(private val server: MemorySegment) : Server {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothServerHandle.destroy(server)
  }

  override suspend fun accept(): Channel = withContext(Dispatchers.IO) {
    NativeBluetoothChannel(BluetoothServerHandle.accept(server))
  }

  override fun close() {
    cleanable.clean()
  }
}
