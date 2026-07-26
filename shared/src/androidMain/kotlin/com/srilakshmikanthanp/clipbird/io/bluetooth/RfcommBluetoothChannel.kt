package com.srilakshmikanthanp.clipbird.io.bluetooth;

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

@SuppressLint("MissingPermission")
class RfcommBluetoothChannel(
  private val socket: BluetoothSocket,
) : BluetoothChannel {
  private val output = DataOutputStream(BufferedOutputStream(socket.outputStream))
  private val input = DataInputStream(BufferedInputStream(socket.inputStream))

  override val remoteAddress: String get() = socket.remoteDevice.address

  override suspend fun readExactly(size: Int): ByteArray = withContext(Dispatchers.IO) {
    runCatching {
      val buffer = ByteArray(size)
      input.readFully(buffer)
      buffer
    }.onFailure {
      close()
    }.getOrThrow()
  }

  override suspend fun write(data: ByteArray, offset: Int, length: Int) = withContext(Dispatchers.IO) {
    runCatching {
      output.write(data, offset, length)
      output.flush()
    }.onFailure {
      close()
    }.getOrThrow()
  }

  override fun close() {
    socket.close()
  }
}
