package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceService
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PeerModule {
  @Single
  fun channelHub(
    pairedDeviceService: BluetoothPairedDeviceService,
    scope: CoroutineScope,
  ): BluetoothPeerHub = BluetoothPeerHub(pairedDeviceService, scope)
}
