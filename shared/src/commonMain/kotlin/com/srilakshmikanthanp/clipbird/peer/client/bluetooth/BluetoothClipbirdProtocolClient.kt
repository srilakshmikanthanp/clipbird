package com.srilakshmikanthanp.clipbird.peer.client.bluetooth

import com.srilakshmikanthanp.clipbird.pairing.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientConnector
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientHandshakeProtocol
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.client.ConnectionInitiationDecider
import kotlinx.coroutines.CoroutineScope

class BluetoothClipbirdProtocolClient(
  activeDeviceProvider: ActivePairedDeviceProvider<BluetoothPairedDevice>,
  connector: ClipbirdClientConnector<BluetoothPairedDevice>,
  decider: ConnectionInitiationDecider,
  handshakeProtocol: ClipbirdClientHandshakeProtocol,
  peerHub: BluetoothPeerHub,
  scope: CoroutineScope,
) : ClipbirdProtocolClient<BluetoothPairedDevice>(
  activeDeviceProvider,
  connector,
  decider,
  handshakeProtocol,
  peerHub,
  scope,
)
