package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.BluetoothClipbirdProtocolServer
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.ClipbirdBluetoothServer
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

@Module
class ClipbirdServerModule {
  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun clipbirdBluetoothServer(manager: BluetoothManager): ClipbirdBluetoothServer = ClipbirdBluetoothServer(
    manager,
    BluetoothServerConfig(manager.name, BluetoothConstants.clipbirdServiceUuid)
  )

  @Single
  fun clipbirdServer(server: ClipbirdBluetoothServer): ClipbirdServer = RetryingClipbirdServer(server)

  @Single
  fun clipbirdServerHandshakeProtocol(
    service: BluetoothPairedDeviceService,
    hostDeviceProvider: HostDeviceProvider,
  ): ClipbirdServerHandshakeProtocol<BluetoothPairedDevice> = ClipbirdServerHandshakeProtocol(
    service,
    hostDeviceProvider
  )

  @Single
  fun clipbirdServerCoordinator(
    advertiser: Advertiser,
    server: ClipbirdServer,
    handshakeProtocol: ClipbirdServerHandshakeProtocol<BluetoothPairedDevice>,
    peerHub: BluetoothPeerHub,
    scope: CoroutineScope,
  ): BluetoothClipbirdProtocolServer = BluetoothClipbirdProtocolServer(
    advertiser,
    server,
    handshakeProtocol,
    peerHub,
    scope,
  )
}
