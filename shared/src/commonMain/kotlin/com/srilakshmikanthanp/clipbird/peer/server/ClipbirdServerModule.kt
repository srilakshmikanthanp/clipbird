package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.peer.server.bluetooth.ClipbirdBluetoothServer
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
  fun clipbirdServer(server: ClipbirdBluetoothServer): ClipbirdServer = server

  @Single
  fun clipbirdServerHandshakeProtocol(
    service: BluetoothPairedDeviceService,
    authenticator: Authenticator,
  ): ClipbirdServerHandshakeProtocol<BluetoothPairedDevice> = ClipbirdServerHandshakeProtocol(service, authenticator)

  @Single
  fun clipbirdServerCoordinator(
    advertiser: Advertiser,
    server: ClipbirdServer,
    handshakeProtocol: ClipbirdServerHandshakeProtocol<BluetoothPairedDevice>,
  ): ClipbirdServerCoordinator = ClipbirdServerCoordinator(advertiser, server, handshakeProtocol)
}