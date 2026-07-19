package com.srilakshmikanthanp.clipbird.ffi.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.MemorySegment

object BluetoothServerHandle {
  fun accept(server: MemorySegment): MemorySegment {
    return Clipbird.clipbird_io_bluetooth_server_accept(server).orThrow { IOException("Failed to accept connection: ${NativeErrorHandle.lastErrorMessage()}") }
  }

  fun destroy(server: MemorySegment) {
    Clipbird.clipbird_io_bluetooth_server_destroy(server)
  }
}
