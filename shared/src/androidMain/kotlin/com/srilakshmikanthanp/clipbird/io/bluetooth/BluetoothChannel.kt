package com.srilakshmikanthanp.clipbird.io.bluetooth;

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

@SuppressLint("MissingPermission")
class BluetoothChannel(
  private val socket: BluetoothSocket,
) : Channel {
  private val output = DataOutputStream(BufferedOutputStream(socket.outputStream))
  private val input = DataInputStream(BufferedInputStream(socket.inputStream))

  private val _isOpen = MutableStateFlow(socket.isConnected)
  override val isOpen: StateFlow<Boolean> = _isOpen.asStateFlow()

  override suspend fun readExactly(size: Int): ByteArray = withContext(Dispatchers.IO) {
    runCatching {
      val buffer = ByteArray(size)
      input.readFully(buffer)
      buffer
    }.onFailure {
      close()
    }.getOrThrow()
  }

  override suspend fun write(data: ByteArray) = withContext(Dispatchers.IO) {
    runCatching {
      output.write(data)
      output.flush()
    }.onFailure {
      close()
    }.getOrThrow()
  }

  override fun close() {
    if (_isOpen.compareAndSet(expect = true, update = false)) {
      socket.close()
    }
  }
}
