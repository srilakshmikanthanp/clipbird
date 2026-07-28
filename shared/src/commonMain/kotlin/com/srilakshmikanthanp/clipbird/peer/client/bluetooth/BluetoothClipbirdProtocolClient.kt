package com.srilakshmikanthanp.clipbird.peer.client.bluetooth

import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.ChannelConnectionChecker
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientConnector
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientHandshakeProtocol
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.client.ConnectionInitiationDecider

class BluetoothClipbirdProtocolClient(
  activeDeviceProvider: ActivePairedDeviceProvider<BluetoothPairedDevice>,
  connector: ClipbirdClientConnector<BluetoothPairedDevice>,
  decider: ConnectionInitiationDecider,
  connectionChecker: ChannelConnectionChecker,
  handshakeProtocol: ClipbirdClientHandshakeProtocol
) : ClipbirdProtocolClient<BluetoothPairedDevice>(
  activeDeviceProvider,
  connector,
  decider,
  connectionChecker,
  handshakeProtocol
)
