package com.srilakshmikanthanp.clipbird.server.bluetooth

import com.srilakshmikanthanp.clipbird.server.ClipbirdServer
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BluetoothClipbirdServer(
  private val bluetoothManager: BluetoothManager,
  private val serverConfig: BluetoothServerConfig,
) : ClipbirdServer {
  override val channels: Flow<Channel> = channelFlow {
    val server = bluetoothManager.start(serverConfig)

    val job = launch {
      server.use { server ->
        while (isActive) {
          send(server.accept())
        }
      }
    }

    awaitClose {
      server.close()
      job.cancel()
    }
  }
}
