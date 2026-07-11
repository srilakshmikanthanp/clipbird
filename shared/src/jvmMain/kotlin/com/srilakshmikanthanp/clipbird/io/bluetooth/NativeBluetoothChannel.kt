package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothChannelHandle
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.foreign.MemorySegment

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
