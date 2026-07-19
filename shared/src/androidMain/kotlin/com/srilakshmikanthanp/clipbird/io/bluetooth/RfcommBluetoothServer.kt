package com.srilakshmikanthanp.clipbird.io.bluetooth;

import android.bluetooth.BluetoothServerSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RfcommBluetoothServer(
  private val bluetoothServerSocket: BluetoothServerSocket,
) : BluetoothServer {
  override suspend fun accept(): BluetoothChannel = withContext(Dispatchers.IO) {
    RfcommBluetoothChannel(bluetoothServerSocket.accept())
  }

  override fun close() {
    bluetoothServerSocket.close()
  }
}
