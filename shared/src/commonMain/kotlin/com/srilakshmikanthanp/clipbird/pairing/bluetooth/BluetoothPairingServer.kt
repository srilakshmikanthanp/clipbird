package com.srilakshmikanthanp.clipbird.pairing.bluetooth

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import com.srilakshmikanthanp.clipbird.pairing.PairingServer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

class BluetoothPairingServer(
  private val bluetoothManager: BluetoothManager,
  private val serverConfig: BluetoothServerConfig,
) : PairingServer<BluetoothChannel> {
  override val channels: Flow<BluetoothChannel> = channelFlow {
    val server = bluetoothManager.start(serverConfig)

    launch {
      server.use {
        try {
          while (isActive) {
            send(it.accept())
          }
        } catch (e: IOException) {
          if (isActive) close(e)
        }
      }
    }

    awaitClose {
      server.close()
    }
  }
}
