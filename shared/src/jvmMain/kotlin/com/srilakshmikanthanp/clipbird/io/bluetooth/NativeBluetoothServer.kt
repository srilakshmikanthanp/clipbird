package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.foreign.MemorySegment
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothServerHandle

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
