package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdClientCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.BluetoothClipbirdServerCoordinator
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PeerModule {
  @Single
  fun channelHub(
    pairedDeviceService: PairedDeviceService<BluetoothPairedDevice>,
    scope: CoroutineScope,
  ): BluetoothChannelHub = BluetoothChannelHub(pairedDeviceService, scope)

  @Single
  fun channelConnectionChecker(
    hub: BluetoothChannelHub,
  ): ChannelConnectionChecker = HubConnectionChecker(hub)

  @Single
  fun channelCoordinator(
    serverCoordinator: BluetoothClipbirdServerCoordinator,
    clientCoordinator: BluetoothClipbirdClientCoordinator,
    channelHub: BluetoothChannelHub,
  ): BluetoothChannelCollector = BluetoothChannelCollector(
    serverCoordinator,
    clientCoordinator,
    channelHub,
  )
}
