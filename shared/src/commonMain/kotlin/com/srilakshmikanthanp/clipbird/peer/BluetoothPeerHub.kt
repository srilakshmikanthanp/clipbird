package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import kotlinx.coroutines.CoroutineScope

class BluetoothPeerHub(
  pairedDeviceService: PairedDeviceService<BluetoothPairedDevice>,
  scope: CoroutineScope
) : PeerHub<BluetoothPairedDevice>(
  pairedDeviceService,
  scope
)
