package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

open class PairingService<PC: PairingCandidate, D: PairedDevice, C: Channel>(
  private val bluetoothProvider: PairingCandidateProvider<PC>,
  private val bluetoothPairer: Pairer<PC, D>,
  private val service: PairedDeviceService<D>,
  private val responder: PairingResponder<C, D>
) {
  private val _pairing = MutableStateFlow<Set<PC>>(emptySet())
  val pairing: StateFlow<Set<PC>> = _pairing.asStateFlow()
  val devices: StateFlow<Collection<PC>> = bluetoothProvider.devices
  private val pairingSemaphore = Semaphore(1)

  suspend fun pair(
    channel: C
  ) = pairingSemaphore.withPermit {
    service.upsert(responder.respond(channel))
  }

  suspend fun pair(
    candidate: PC
  ): D = pairingSemaphore.withPermit {
    try {
      _pairing.value += candidate
      return bluetoothPairer.pair(candidate).also { service.upsert(it) }
    } finally {
      _pairing.value -= candidate
    }
  }
}
