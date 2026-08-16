package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothChannelHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.foreign.MemorySegment

class NativeBluetoothChannel(private val channel: MemorySegment) : BluetoothChannel {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothChannelHandle.destroy(channel)
  }

  private val lock = Any()
  private var inFlight = 0
  private var closed = false

  private inline fun <T> withHandle(block: () -> T): T {
    synchronized(lock) {
      if (closed) throw IOException("Channel is closed")
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

  override val remoteAddress: String get() = withHandle { BluetoothChannelHandle.remoteAddress(channel) }

  override suspend fun readExactly(size: Int): ByteArray = withContext(Dispatchers.IO) {
    withHandle { BluetoothChannelHandle.readExactly(channel, size) }
  }

  override suspend fun write(data: ByteArray, offset: Int, length: Int) = withContext(Dispatchers.IO) {
    withHandle { BluetoothChannelHandle.write(channel, data, offset, length.toLong()) }
  }

  override fun close() {
    synchronized(lock) {
      if (closed) return
      closed = true
    }

    try {
      BluetoothChannelHandle.close(channel)
    } finally {
      tryDestroy()
    }
  }
}
