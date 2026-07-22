package com.srilakshmikanthanp.clipbird.peer.client.bluetooth

import com.srilakshmikanthanp.clipbird.peer.client.ClientServerConnector
import com.srilakshmikanthanp.clipbird.peer.client.ClientServerHandshakeProtocol
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerEndpoint
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothClientServerConnector(
  private val bluetoothManager: BluetoothManager,
  private val serviceUuid: Uuid,
) : ClientServerConnector<BluetoothPairedDevice> {
  override suspend fun connect(pairedDevice: BluetoothPairedDevice): Channel {
    return bluetoothManager.connect(BluetoothServerEndpoint(pairedDevice.address, serviceUuid))
  }
}
