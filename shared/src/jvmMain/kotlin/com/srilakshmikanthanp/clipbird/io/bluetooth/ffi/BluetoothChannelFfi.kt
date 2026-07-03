package com.srilakshmikanthanp.clipbird.io.bluetooth.ffi

import com.srilakshmikanthanp.clipbird.ffi.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.NativeClipbirdLoader.library
import com.srilakshmikanthanp.clipbird.ffi.NativeFfiError
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.use

private object BluetoothChannelFfiBindings {
  private val readExactlyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_channel_read_exactly"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_BOOLEAN,
        ValueLayout.ADDRESS,   // channel*
        ValueLayout.ADDRESS,   // buffer*
        ValueLayout.JAVA_LONG, // length (size_t)
      ),
    )
  }

  fun readExactly(channel: MemorySegment, size: Int): ByteArray {
    Arena.ofConfined().use { arena ->
      val buffer = arena.allocate(size.toLong())
      if (!(readExactlyHandle.invoke(channel, buffer, size.toLong()) as Boolean)) {
        throw IOException("Failed to read from channel: ${NativeFfiError.lastErrorMessage()}")
      }
      return buffer.toArray(ValueLayout.JAVA_BYTE)
    }
  }

  private val writeHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_channel_write"),
      FunctionDescriptor.of(
        ValueLayout.JAVA_BOOLEAN,
        ValueLayout.ADDRESS,   // channel*
        ValueLayout.ADDRESS,   // data*
        ValueLayout.JAVA_LONG, // length (size_t)
      ),
    )
  }

  fun write(channel: MemorySegment, data: ByteArray) {
    Arena.ofConfined().use { arena ->
      val dataSeg = arena.allocate(data.size.toLong())
      MemorySegment.copy(data, 0, dataSeg, ValueLayout.JAVA_BYTE, 0L, data.size)
      if (!(writeHandle.invoke(channel, dataSeg, data.size.toLong()) as Boolean)) {
        throw IOException("Failed to write to channel: ${NativeFfiError.lastErrorMessage()}")
      }
    }
  }

  private val destroyHandle by lazy {
    Linker.nativeLinker().downcallHandle(
      library.findOrThrow("clipbird_channel_destroy"),
      FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
    )
  }

  fun destroy(channel: MemorySegment) {
    destroyHandle.invoke(channel)
  }
}

class BluetoothChannelFfi(private val channel: MemorySegment) : Channel {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothChannelFfiBindings.destroy(channel)
  }

  override suspend fun readExactly(size: Int): ByteArray = withContext(Dispatchers.IO) {
    BluetoothChannelFfiBindings.readExactly(channel, size)
  }

  override suspend fun write(data: ByteArray) = withContext(Dispatchers.IO) {
    BluetoothChannelFfiBindings.write(channel, data)
  }

  override fun close() {
    cleanable.clean()
  }
}
