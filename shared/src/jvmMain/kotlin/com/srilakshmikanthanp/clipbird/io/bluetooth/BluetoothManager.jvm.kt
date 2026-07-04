package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.ClientFactory
import com.srilakshmikanthanp.clipbird.io.Server
import com.srilakshmikanthanp.clipbird.io.ServerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

actual class BluetoothManager : ClientFactory<BluetoothServerEndpoint>, ServerFactory<BluetoothServerConfig> {
  private val nativeBluetoothManager = NativeBluetoothManager()

  actual val boundedDevices: StateFlow<List<BluetoothDevice>> = MutableStateFlow<List<BluetoothDevice>>(emptyList()).asStateFlow()

  override suspend fun connect(endpoint: BluetoothServerEndpoint): Channel = withContext(Dispatchers.IO) {
    NativeBluetoothChannel(nativeBluetoothManager.connect(endpoint))
  }

  override suspend fun start(config: BluetoothServerConfig): Server = withContext(Dispatchers.IO) {
    NativeBluetoothServer(nativeBluetoothManager.start(config))
  }
}
