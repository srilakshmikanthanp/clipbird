package com.srilakshmikanthanp.clipbird.peer.server.bluetooth

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServer
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdProtocolServer
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerHandshakeProtocol

class BluetoothClipbirdProtocolServer(
  advertiser: Advertiser,
  server: ClipbirdServer,
  handshakeProtocol: ClipbirdServerHandshakeProtocol<BluetoothPairedDevice>,
  peerHub: BluetoothPeerHub
) : ClipbirdProtocolServer<BluetoothPairedDevice>(
  advertiser,
  server,
  handshakeProtocol,
  peerHub
)
