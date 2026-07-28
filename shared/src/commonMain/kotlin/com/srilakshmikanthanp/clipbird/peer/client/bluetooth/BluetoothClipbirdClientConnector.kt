package com.srilakshmikanthanp.clipbird.peer.client.bluetooth

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerEndpoint
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientConnector
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class BluetoothClipbirdClientConnector(
  private val bluetoothManager: BluetoothManager,
  private val serviceUuid: Uuid,
) : ClipbirdClientConnector<BluetoothPairedDevice> {
  override suspend fun connect(pairedDevice: BluetoothPairedDevice): Channel {
    return bluetoothManager.connect(BluetoothServerEndpoint(pairedDevice.address, serviceUuid))
  }
}
