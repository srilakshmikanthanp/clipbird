package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.Server

interface BluetoothServer : Server {
  override suspend fun accept(): BluetoothChannel
}
