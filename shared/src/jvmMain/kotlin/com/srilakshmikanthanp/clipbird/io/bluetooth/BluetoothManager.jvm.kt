package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.ClientFactory
import com.srilakshmikanthanp.clipbird.io.Server
import com.srilakshmikanthanp.clipbird.io.ServerFactory
import kotlinx.coroutines.flow.StateFlow

actual class BluetoothManager :
  ClientFactory<BluetoothServerEndpoint>,
  ServerFactory<BluetoothServerConfig> {
  actual val boundedDevices: StateFlow<List<BluetoothDevice>> get() = TODO("Not yet implemented")

  override suspend fun connect(endpoint: BluetoothServerEndpoint): Channel {
    TODO("Not yet implemented")
  }

  override suspend fun start(config: BluetoothServerConfig): Server {
    TODO("Not yet implemented")
  }
}