package com.srilakshmikanthanp.clipbird.peer.client.bluetooth

import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.ChannelConnectionChecker
import com.srilakshmikanthanp.clipbird.peer.client.ClientServerConnectionInitiationDecider
import com.srilakshmikanthanp.clipbird.peer.client.ClientServerConnector
import com.srilakshmikanthanp.clipbird.peer.client.ClientServerHandshakeProtocol
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdProtocolClient

class BluetoothClipbirdProtocolClient (
  activeDeviceProvider: ActivePairedDeviceProvider<BluetoothPairedDevice>,
  connector: ClientServerConnector<BluetoothPairedDevice>,
  decider: ClientServerConnectionInitiationDecider,
  connectionChecker: ChannelConnectionChecker,
  handshakeProtocol: ClientServerHandshakeProtocol
) : ClipbirdProtocolClient<BluetoothPairedDevice>(
  activeDeviceProvider,
  connector,
  decider,
  connectionChecker,
  handshakeProtocol
)