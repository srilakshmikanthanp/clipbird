package com.srilakshmikanthanp.clipbird.peer.server.bluetooth

import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServer
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

class ClipbirdBluetoothServer(
  private val bluetoothManager: BluetoothManager,
  private val serverConfig: BluetoothServerConfig,
) : ClipbirdServer {
  override val channels: Flow<Channel> = channelFlow {
    val server = bluetoothManager.start(serverConfig)

    launch {
      try {
        server.use { server ->
          while (isActive) {
            send(server.accept())
          }
        }
      } catch (e: IOException) {
        if (isActive) close(e)
      }
    }

    awaitClose {
      server.close()
    }
  }
}
