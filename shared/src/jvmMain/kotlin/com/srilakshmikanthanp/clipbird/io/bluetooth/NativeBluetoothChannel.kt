package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothChannelHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.foreign.MemorySegment

class NativeBluetoothChannel(private val channel: MemorySegment) : BluetoothChannel {
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothChannelHandle.destroy(channel)
  }

  override val remoteAddress: String get() = BluetoothChannelHandle.remoteAddress(channel)

  override suspend fun readExactly(size: Int): ByteArray = withContext(Dispatchers.IO) {
    BluetoothChannelHandle.readExactly(channel, size)
  }

  override suspend fun write(data: ByteArray, offset: Int, length: Int) = withContext(Dispatchers.IO) {
    BluetoothChannelHandle.write(channel, data, offset, length.toLong())
  }

  override fun close() {
    cleanable.clean()
  }
}
