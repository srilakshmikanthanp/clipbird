package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.paring.bluetooth.BluetoothPairingCandidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PairingCoordinator(
  private val bluetoothProvider: PairingCandidateProvider<BluetoothPairingCandidate>,
  private val bluetoothPairer: Pairer<BluetoothPairingCandidate, BluetoothPairedDevice>,
) {
  val devices: StateFlow<Collection<PairingCandidate>> = bluetoothProvider.devices
  private val _pairing = MutableStateFlow<Set<PairingCandidate>>(emptySet())
  val pairing: StateFlow<Set<PairingCandidate>> = _pairing.asStateFlow()
  private val lock = Mutex()

  private suspend fun doPair(candidate: PairingCandidate): PairedDevice {
    return when (candidate) {
      is BluetoothPairingCandidate -> bluetoothPairer.pair(candidate)
      else -> throw IllegalPairingCandidateException(candidate)
    }
  }

  suspend fun pair(candidate: PairingCandidate): PairedDevice {
    lock.withLock {
      if (candidate in _pairing.value) {
        throw AlreadyPairingException("Already pairing with ${candidate.name}")
      } else {
        _pairing.value += candidate
      }
    }

    return try {
      doPair(candidate)
    } finally {
      lock.withLock { _pairing.value -= candidate }
    }
  }
}
