package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothServerHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.foreign.MemorySegment

class NativeBluetoothServer(private val server: MemorySegment) : BluetoothServer {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothServerHandle.destroy(server)
  }

  private val lock = Any()
  private var inFlight = 0
  private var closed = false

  private inline fun <T> withHandle(block: () -> T): T {
    synchronized(lock) {
      if (closed) throw IOException("Server is closed")
      inFlight++
    }

    try {
      return block()
    } finally {
      synchronized(lock) { inFlight-- }
      tryDestroy()
    }
  }

  private fun tryDestroy() {
    if (synchronized(lock) { closed && inFlight == 0 }) {
      cleanable.clean()
    }
  }

  override suspend fun accept(): BluetoothChannel = withContext(Dispatchers.IO) {
    withHandle { NativeBluetoothChannel(BluetoothServerHandle.accept(server)) }
  }

  override fun close() {
    synchronized(lock) {
      if (closed) return
      closed = true
    }

    try {
      BluetoothServerHandle.close(server)
    } finally {
      tryDestroy()
    }
  }
}
