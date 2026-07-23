package com.srilakshmikanthanp.clipbird.peer.client

import com.srilakshmikanthanp.clipbird.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.paring.ActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.ble.BleActivePairedDeviceProvider
import com.srilakshmikanthanp.clipbird.peer.ChannelConnectionChecker
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClientServerConnector
import com.srilakshmikanthanp.clipbird.peer.client.bluetooth.BluetoothClipbirdClientCoordinator
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
    deviceTimeout = 5.seconds,
  )

  @Single
  fun blePairedActiveDeviceProvider(
    discoverer: BleDiscoverer,
    service: BluetoothPairedDeviceService,
  ): BleActivePairedDeviceProvider = BleActivePairedDeviceProvider(discoverer, service)

  @Single
  fun pairedActiveDeviceProvider(
    provider: BleActivePairedDeviceProvider,
  ): ActivePairedDeviceProvider<BluetoothPairedDevice> = provider

  @Single
  fun connectionInitiationDecider(
    hostDeviceProvider: HostDeviceProvider,
  ): ClientServerConnectionInitiationDecider = ClientServerConnectionInitiationDecider(hostDeviceProvider)

  @Single
  fun clientServerHandshakeProtocol(
    authenticator: Authenticator,
    hostDeviceProvider: HostDeviceProvider,
  ): ClientServerHandshakeProtocol = ClientServerHandshakeProtocol(authenticator, hostDeviceProvider)

  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bluetoothClientServerConnector(
    manager: BluetoothManager
  ): BluetoothClientServerConnector = BluetoothClientServerConnector(
    manager,
    BluetoothConstants.clipbirdServiceUuid,
  )

  @Single
  fun clipbirdClientCoordinator(
    activeDeviceProvider: BleActivePairedDeviceProvider,
    connector: BluetoothClientServerConnector,
    decider: ClientServerConnectionInitiationDecider,
    connectionChecker: ChannelConnectionChecker,
    handshakeProtocol: ClientServerHandshakeProtocol,
  ): BluetoothClipbirdClientCoordinator = BluetoothClipbirdClientCoordinator(
    activeDeviceProvider,
    connector,
    decider,
    connectionChecker,
    handshakeProtocol
  )
}
