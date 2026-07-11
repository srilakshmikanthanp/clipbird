package com.srilakshmikanthanp.clipbird.ffi.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use

object BluetoothChannelHandle {
  fun readExactly(channel: MemorySegment, size: Int): ByteArray {
    Arena.ofConfined().use { arena ->
      val buffer: MemorySegment = arena.allocate(size.toLong())
      Clipbird.clipbird_io_channel_read_exactly(channel, buffer, size.toLong()).orThrow { NativeErrorHandle.lastErrorCode().toChannelReadExactlyException() }
      return buffer.toArray(ValueLayout.JAVA_BYTE)
    }
  }

  fun write(channel: MemorySegment, data: ByteArray) {
    Clipbird.clipbird_io_channel_write(channel, MemorySegment.ofArray(data), data.size.toLong()).orThrow {
      IOException("Failed to write to channel: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(channel: MemorySegment) {
    Clipbird.clipbird_io_channel_destroy(channel)
  }
}
