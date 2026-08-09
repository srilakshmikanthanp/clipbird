package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.pairing.RetryingActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.ble.BleActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdClientConnector
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdProtocolClient
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

@Module
class ClipbirdClientModule {
  @Single
  fun connectionInitiationDecider(
    hostDeviceProvider: HostDeviceProvider,
  ): ConnectionInitiationDecider = ConnectionInitiationDecider(
    hostDeviceProvider
  )

  @Single
  fun clipbirdClientHandshakeProtocol(
    hostDeviceProvider: HostDeviceProvider,
  ): ClipbirdClientHandshakeProtocol = ClipbirdClientHandshakeProtocol(
    hostDeviceProvider
  )

  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bluetoothClipbirdClientConnector(
    manager: BluetoothManager
  ): BluetoothClipbirdClientConnector = BluetoothClipbirdClientConnector(
    manager,
    BluetoothConstants.clipbirdServiceUuid,
  )

  @Single
  fun clipbirdClientCoordinator(
    activeDeviceProvider: BleActivePairedDeviceProvider,
    connector: BluetoothClipbirdClientConnector,
    decider: ConnectionInitiationDecider,
    handshakeProtocol: ClipbirdClientHandshakeProtocol,
    peerHub: BluetoothPeerHub,
    scope: CoroutineScope,
  ): BluetoothClipbirdProtocolClient = BluetoothClipbirdProtocolClient(
    RetryingActivePairedDeviceProvider(activeDeviceProvider),
    connector,
    decider,
    handshakeProtocol,
    peerHub,
    scope,
  )
}