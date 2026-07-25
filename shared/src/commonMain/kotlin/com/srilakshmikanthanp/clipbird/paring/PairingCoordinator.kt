package com.srilakshmikanthanp.clipbird.paring

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

open class PairingCoordinator<Candidate: PairingCandidate, Device: PairedDevice>(
  private val bluetoothProvider: PairingCandidateProvider<Candidate>,
  private val bluetoothPairer: Pairer<Candidate, Device>,
  private val pairingServer: PairingServer<BluetoothChannel>,
  private val service: PairedDeviceService<Device>,
  private val scope: CoroutineScope,
  private val responder: PairingResponder<BluetoothChannel, Device>
) {
  private val _pairing = MutableStateFlow<Set<Candidate>>(emptySet())
  val pairing: StateFlow<Set<Candidate>> = _pairing.asStateFlow()
  val devices: StateFlow<Collection<Candidate>> = bluetoothProvider.devices
  private val pairingSemaphore = Semaphore(1)

  suspend fun pair(candidate: Candidate): Device = pairingSemaphore.withPermit {
    _pairing.value += candidate

    try {
      return bluetoothPairer.pair(candidate).also { service.upsert(it) }
    } finally {
      _pairing.value -= candidate
    }
  }

  private suspend fun onNewChannel(channel: BluetoothChannel) = pairingSemaphore.withPermit {
    try {
      channel.use { service.upsert(responder.respond(it)) }
    } catch (e: Exception) {
      Logger.e("Error pairing to channel: ${e.message}", e, TAG)
    }
  }

  private suspend fun doCollect() {
    try {
      pairingServer.channels.collect { onNewChannel(it) }
    } catch (e: Exception) {
      Logger.e("Error collecting channels: ${e.message}", e, TAG)
    }
  }

  suspend fun run() {
    doCollect()
  }

  companion object {
    const val TAG = "PairingCoordinator"
  }
}
