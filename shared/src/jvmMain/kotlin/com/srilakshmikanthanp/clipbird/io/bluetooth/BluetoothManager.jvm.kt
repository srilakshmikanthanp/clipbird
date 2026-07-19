package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.ClientFactory
import com.srilakshmikanthanp.clipbird.io.ServerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

actual class BluetoothManager : ClientFactory<BluetoothServerEndpoint>, ServerFactory<BluetoothServerConfig> {
  private val nativeBluetoothManager = NativeBluetoothManager()

  private val _boundedDevices = MutableStateFlow(nativeBluetoothManager.bondedDevices())

  actual val boundedDevices: StateFlow<List<BluetoothDevice>> = _boundedDevices.asStateFlow()

  actual val name: String get() = nativeBluetoothManager.name()

  init {
    nativeBluetoothManager.setBondedDevicesChangedCallback {
      _boundedDevices.value = nativeBluetoothManager.bondedDevices()
    }
  }

  actual override suspend fun connect(endpoint: BluetoothServerEndpoint): BluetoothChannel = withContext(Dispatchers.IO) {
    NativeBluetoothChannel(nativeBluetoothManager.connect(endpoint))
  }

  actual override suspend fun start(config: BluetoothServerConfig): BluetoothServer = withContext(Dispatchers.IO) {
    NativeBluetoothServer(nativeBluetoothManager.start(config))
  }
}
