package com.srilakshmikanthanp.clipbird.peer.server.bluetooth

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServer
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerCoordinator
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerHandshakeProtocol

class BluetoothClipbirdServerCoordinator(
  advertiser: Advertiser,
  server: ClipbirdServer,
  handshakeProtocol: ClipbirdServerHandshakeProtocol<BluetoothPairedDevice>
) : ClipbirdServerCoordinator<BluetoothPairedDevice>(
  advertiser,
  server,
  handshakeProtocol
)
