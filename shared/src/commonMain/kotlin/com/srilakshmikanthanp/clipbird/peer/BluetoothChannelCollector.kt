package com.srilakshmikanthanp.clipbird.peer

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerCoordinator
import kotlinx.coroutines.CoroutineScope

class BluetoothChannelCollector(
  serverCoordinator: ClipbirdServerCoordinator<BluetoothPairedDevice>,
  clientCoordinator: ClipbirdClientCoordinator<BluetoothPairedDevice>,
  channelHub: ChannelHub<BluetoothPairedDevice>,
  scope: CoroutineScope,
) : ChannelCollector<BluetoothPairedDevice>(
  serverCoordinator,
  clientCoordinator,
  channelHub,
  scope
)
