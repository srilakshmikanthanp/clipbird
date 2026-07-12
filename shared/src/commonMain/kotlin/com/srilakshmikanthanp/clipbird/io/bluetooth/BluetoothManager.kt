package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.io.ClientFactory
import com.srilakshmikanthanp.clipbird.io.ServerFactory
import kotlinx.coroutines.flow.StateFlow

expect class BluetoothManager : ClientFactory<BluetoothServerEndpoint>, ServerFactory<BluetoothServerConfig> {
  val boundedDevices: StateFlow<List<BluetoothDevice>>
  val name: String
}
