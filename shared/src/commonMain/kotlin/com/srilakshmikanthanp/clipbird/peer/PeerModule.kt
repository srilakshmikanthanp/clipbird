package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.BluetoothClipbirdProtocolServer
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class PeerModule {
  @Single
  fun channelHub(
    pairedDeviceService: PairedDeviceService<BluetoothPairedDevice>,
    scope: CoroutineScope,
  ): BluetoothPeerHub = BluetoothPeerHub(pairedDeviceService, scope)

  @Single
  fun channelConnectionChecker(
    hub: BluetoothPeerHub,
  ): ChannelConnectionChecker = HubConnectionChecker(hub)

  @Single
  fun channelCoordinator(
    serverCoordinator: BluetoothClipbirdProtocolServer,
    clientCoordinator: BluetoothClipbirdProtocolClient,
    channelHub: BluetoothPeerHub,
  ): BluetoothChannelCollector = BluetoothChannelCollector(
    serverCoordinator,
    clientCoordinator,
    channelHub,
  )
}
