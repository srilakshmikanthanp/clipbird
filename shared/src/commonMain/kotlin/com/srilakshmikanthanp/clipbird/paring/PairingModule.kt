package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothServerConfig
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceDao
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairer
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidateProvider
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingServer
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingServerResponder
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

@Module
class PairingModule {
  @Single
  fun bluetoothPairedDeviceService(
    pairedDao: PairedDeviceEntityDao,
    bleDao: BluetoothPairedDeviceDao,
  ): BluetoothPairedDeviceService = BluetoothPairedDeviceService(
    pairedDao, bleDao
  )

  @Single
  fun pairedDeviceService(service: BluetoothPairedDeviceService): PairedDeviceService<out PairedDevice> = service

  @Single
  fun blockingPairingVerifier(): PairingDeferredVerifier = PairingDeferredVerifier()

  @Single
  fun pairingVerifier(verifier: PairingDeferredVerifier): PairingVerifier = verifier

  @Single
  fun bluetoothPairingCandidateProvider(
    service: BluetoothPairedDeviceService,
    scope: CoroutineScope,
    manager: BluetoothManager,
  ): BluetoothPairingCandidateProvider = BluetoothPairingCandidateProvider(
    service,
    scope,
    manager
  )

  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bluetoothPairer(
    manager: BluetoothManager,
    hostDeviceProvider: HostDeviceProvider,
    verifier: PairingVerifier,
  ): BluetoothPairer = BluetoothPairer(
    manager,
    hostDeviceProvider,
    verifier,
    BluetoothConstants.clipbirdPairingServiceUuid
  )

  @Single
  fun serverPairingResponder(
    hostDeviceProvider: HostDeviceProvider,
    verifier: PairingVerifier,
  ): BluetoothPairingServerResponder = BluetoothPairingServerResponder(
    hostDeviceProvider,
    verifier
  )

  @Single
  fun pairingResponder(
    responder: BluetoothPairingServerResponder
  ): PairingResponder<BluetoothChannel, BluetoothPairedDevice> = responder

  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bluetoothPairingServer(
    manager: BluetoothManager,
  ): BluetoothPairingServer = BluetoothPairingServer(
    manager,
    BluetoothServerConfig(manager.name, BluetoothConstants.clipbirdPairingServiceUuid),
  )

  @Single
  fun pairingServer(
    server: BluetoothPairingServer
  ): PairingServer<BluetoothChannel> = server

  @Single
  fun pairingCoordinator(
    provider: BluetoothPairingCandidateProvider,
    pairer: BluetoothPairer,
    server: PairingServer<BluetoothChannel>,
    service: BluetoothPairedDeviceService,
    scope: CoroutineScope,
    responder: PairingResponder<BluetoothChannel, BluetoothPairedDevice>,
  ): PairingCoordinator = PairingCoordinator(
    provider,
    pairer,
    server,
    service,
    scope,
    responder,
  )
}
