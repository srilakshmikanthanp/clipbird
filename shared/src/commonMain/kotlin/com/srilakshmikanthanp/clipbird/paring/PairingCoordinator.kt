package com.srilakshmikanthanp.clipbird.paring

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class PairingCoordinator(
  private val bluetoothProvider: PairingCandidateProvider<BluetoothPairingCandidate>,
  private val bluetoothPairer: Pairer<BluetoothPairingCandidate, BluetoothPairedDevice>,
  private val pairingServer: PairingServer<BluetoothChannel>,
  private val service: BluetoothPairedDeviceService,
  private val scope: CoroutineScope,
  private val responder: PairingResponder<BluetoothChannel, BluetoothPairedDevice>
) {
  val devices: StateFlow<Collection<PairingCandidate>> = bluetoothProvider.devices

  private val _pairing = MutableStateFlow<Set<PairingCandidate>>(emptySet())
  val pairing: StateFlow<Set<PairingCandidate>> = _pairing.asStateFlow()

  private val pairingSemaphore = Semaphore(1)

  private val serverJobDelegate = lazy { scope.launch { doCollect() } }
  private val serverJob by serverJobDelegate

  private suspend fun doPair(candidate: BluetoothPairingCandidate): BluetoothPairedDevice {
    return bluetoothPairer.pair(candidate).also {
      service.upsert(it)
    }
  }

  suspend fun pair(candidate: PairingCandidate): PairedDevice {
    pairingSemaphore.withPermit {
      _pairing.value += candidate

      return try {
        when (candidate) {
          is BluetoothPairingCandidate -> doPair(candidate)
          else -> throw IllegalPairingCandidateException(candidate)
        }
      } finally {
        _pairing.value -= candidate
      }
    }
  }

  private suspend fun onNewChannel(channel: BluetoothChannel) {
    pairingSemaphore.withPermit {
      try {
        channel.use { service.upsert(responder.respond(it)) }
      } catch (e: Exception) {
        Logger.e("Error pairing to channel: ${e.message}", e, TAG)
      }
    }
  }

  private suspend fun doCollect() {
    try {
      pairingServer.channels.collect { onNewChannel(it) }
    } catch (e: Exception) {
      Logger.e("Error collecting channels: ${e.message}", e, TAG)
    }
  }

  fun start() {
    serverJob
  }

  fun stop() {
    if (serverJobDelegate.isInitialized()) {
      serverJob.cancel()
    }
  }

  companion object {
    const val TAG = "PairingCoordinator"
  }
}
