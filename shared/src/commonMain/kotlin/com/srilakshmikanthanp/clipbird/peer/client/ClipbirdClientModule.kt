package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.RetryingDiscoverer
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.pairing.RetryingActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.ble.BleActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdClientConnector
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdProtocolClient
import com.srilakshmikanthanp.clipbird.peer.handshake.authentication.Authenticator
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

@Module
class ClipbirdClientModule {
  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bleDiscoverer(): BleDiscoverer = BleDiscoverer(
    serviceUuid = BluetoothConstants.clipbirdServiceUuid,
    deviceTimeout = 30.seconds,
  )

  @Single
  fun blePairedActiveDeviceProvider(
    discoverer: BleDiscoverer,
    service: BluetoothPairedDeviceService,
  ): BleActivePairedDeviceProvider = BleActivePairedDeviceProvider(
    RetryingDiscoverer(discoverer),
    service
  )

  @Single
  fun connectionInitiationDecider(
    hostDeviceProvider: HostDeviceProvider,
  ): ConnectionInitiationDecider = ConnectionInitiationDecider(
    hostDeviceProvider
  )

  @Single
  fun clipbirdClientHandshakeProtocol(
    authenticator: Authenticator,
    hostDeviceProvider: HostDeviceProvider,
  ): ClipbirdClientHandshakeProtocol = ClipbirdClientHandshakeProtocol(
    authenticator,
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
  ): BluetoothClipbirdProtocolClient = BluetoothClipbirdProtocolClient(
    RetryingActivePairedDeviceProvider(activeDeviceProvider),
    connector,
    decider,
    handshakeProtocol,
    peerHub
  )
}