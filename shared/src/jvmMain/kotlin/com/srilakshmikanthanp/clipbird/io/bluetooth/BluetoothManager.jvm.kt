package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.ClientFactory
import com.srilakshmikanthanp.clipbird.io.Server
import com.srilakshmikanthanp.clipbird.io.ServerFactory
import com.srilakshmikanthanp.clipbird.io.bluetooth.ffi.BluetoothChannelFfi
import com.srilakshmikanthanp.clipbird.io.bluetooth.ffi.BluetoothManagerFfi
import com.srilakshmikanthanp.clipbird.io.bluetooth.ffi.BluetoothServerFfi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

actual class BluetoothManager : ClientFactory<BluetoothServerEndpoint>, ServerFactory<BluetoothServerConfig> {
  private val bluetoothManagerFfi = BluetoothManagerFfi()

  actual val boundedDevices: StateFlow<List<BluetoothDevice>> = MutableStateFlow<List<BluetoothDevice>>(emptyList()).asStateFlow()

  override suspend fun connect(endpoint: BluetoothServerEndpoint): Channel = withContext(Dispatchers.IO) {
    BluetoothChannelFfi(bluetoothManagerFfi.connect(endpoint))
  }

  override suspend fun start(config: BluetoothServerConfig): Server = withContext(Dispatchers.IO) {
    BluetoothServerFfi(bluetoothManagerFfi.start(config))
  }
}
