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
      Clipbird.clipbird_io_bluetooth_channel_read_exactly(channel, buffer, size.toLong()).orThrow { NativeErrorHandle.lastErrorCode().toChannelReadExactlyException() }
      return buffer.toArray(ValueLayout.JAVA_BYTE)
    }
  }

  fun write(channel: MemorySegment, data: ByteArray, offset: Int, length: Long) {
    Arena.ofConfined().use { arena ->
      val buffer = arena.allocate(length)
      buffer.copyFrom(MemorySegment.ofArray(data).asSlice(offset.toLong(), length))
      Clipbird.clipbird_io_bluetooth_channel_write(channel, buffer, length).orThrow {
        IOException("Failed to write to channel: ${NativeErrorHandle.lastErrorMessage()}")
      }
    }
  }

  fun remoteAddress(channel: MemorySegment): String {
    return Clipbird.clipbird_io_bluetooth_channel_remote_address(channel).orThrow {
      IOException("Failed to get remote address: ${NativeErrorHandle.lastErrorMessage()}")
    }.getString(0)
  }

  fun close(channel: MemorySegment) {
    Clipbird.clipbird_io_bluetooth_channel_close(channel).orThrow {
      IOException("Failed to close channel: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(channel: MemorySegment) {
    Clipbird.clipbird_io_bluetooth_channel_destroy(channel)
  }
}
