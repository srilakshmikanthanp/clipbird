package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import kotlinx.coroutines.CoroutineScope

class BluetoothPeerHub(
  pairedDeviceService: PairedDeviceService<BluetoothPairedDevice>,
  scope: CoroutineScope
) : PeerHub<BluetoothPairedDevice>(
  pairedDeviceService,
  scope
)
