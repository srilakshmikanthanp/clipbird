package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
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
  fun clipbirdServer(server: ClipbirdBluetoothServer): ClipbirdServer = server

  @Single
  fun clipbirdServerCoordinator(
    advertiser: Advertiser,
    server: ClipbirdServer,
    scope: CoroutineScope,
  ): ClipbirdServerCoordinator = ClipbirdServerCoordinator(advertiser, server, scope)
}
