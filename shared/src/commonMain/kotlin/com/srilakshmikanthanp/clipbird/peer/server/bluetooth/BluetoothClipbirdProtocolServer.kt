package com.srilakshmikanthanp.clipbird.peer.server.bluetooth

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServer
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdProtocolServer
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerHandshakeProtocol

class BluetoothClipbirdProtocolServer(
  advertiser: Advertiser,
  server: ClipbirdServer,
  handshakeProtocol: ClipbirdServerHandshakeProtocol<BluetoothPairedDevice>
) : ClipbirdProtocolServer<BluetoothPairedDevice>(
  advertiser,
  server,
  handshakeProtocol
)
