package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceDao
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairer
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidateProvider
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

/** Pairing engine: paired-device storage, discovery, the verifier and the coordinator. */
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
  fun blockingPairingVerifier(): BlockingPairingVerifier = BlockingPairingVerifier()

  @Single
  fun pairingVerifier(verifier: BlockingPairingVerifier): PairingVerifier = verifier

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
    BluetoothConstants.clipbirdServiceUuid
  )

  @Single
  fun pairingCoordinator(
    provider: BluetoothPairingCandidateProvider,
    pairer: BluetoothPairer,
  ): PairingCoordinator = PairingCoordinator(
    provider,
    pairer
  )
}
