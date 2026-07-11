package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.extensions.orThrow
import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.error.NativeError
import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.toChannelReadExactlyException
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use

private object BluetoothChannelHandle {
  fun readExactly(channel: MemorySegment, size: Int): ByteArray {
    Arena.ofConfined().use { arena ->
      val buffer: MemorySegment = arena.allocate(size.toLong())
      Clipbird.clipbird_io_channel_read_exactly(channel, buffer, size.toLong()).orThrow { NativeError.lastErrorCode().toChannelReadExactlyException() }
      return buffer.toArray(ValueLayout.JAVA_BYTE)
    }
  }

  fun write(channel: MemorySegment, data: ByteArray) {
    Clipbird.clipbird_io_channel_write(channel, MemorySegment.ofArray(data), data.size.toLong()).orThrow {
      IOException("Failed to write to channel: ${NativeError.lastErrorMessage()}")
    }
  }

  fun destroy(channel: MemorySegment) {
    Clipbird.clipbird_io_channel_destroy(channel)
  }
}

class NativeBluetoothChannel(private val channel: MemorySegment) : Channel {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothChannelHandle.destroy(channel)
  }

  override suspend fun readExactly(size: Int): ByteArray = withContext(Dispatchers.IO) {
    BluetoothChannelHandle.readExactly(channel, size)
  }

  override suspend fun write(data: ByteArray) = withContext(Dispatchers.IO) {
    BluetoothChannelHandle.write(channel, data)
  }

  override fun close() {
    cleanable.clean()
  }
}
