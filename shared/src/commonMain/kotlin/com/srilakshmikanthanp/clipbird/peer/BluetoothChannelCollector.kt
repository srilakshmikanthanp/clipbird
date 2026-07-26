package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdProtocolServer

class BluetoothChannelCollector(
  serverCoordinator: ClipbirdProtocolServer<BluetoothPairedDevice>,
  clientCoordinator: ClipbirdProtocolClient<BluetoothPairedDevice>,
  peerHub: PeerHub<BluetoothPairedDevice>,
) : ChannelCollector<BluetoothPairedDevice>(
  serverCoordinator,
  clientCoordinator,
  peerHub,
)
