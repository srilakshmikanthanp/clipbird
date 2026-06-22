package com.srilakshmikanthanp.clipbird.io.bluetooth;

import android.bluetooth.BluetoothServerSocket
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BluetoothServer(
  private val bluetoothServerSocket: BluetoothServerSocket,
) : Server {
  override suspend fun accept(): Channel = withContext(Dispatchers.IO) {
    BluetoothChannel(bluetoothServerSocket.accept())
  }

  override fun close() {
    bluetoothServerSocket.close()
  }
}
