package com.srilakshmikanthanp.clipbird.ui.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.paring.AlreadyPairingException
import com.srilakshmikanthanp.clipbird.paring.IllegalPairingCandidateException
import com.srilakshmikanthanp.clipbird.paring.PairedDevice
import com.srilakshmikanthanp.clipbird.paring.PairedDeviceService
import com.srilakshmikanthanp.clipbird.paring.PairingCandidate
import com.srilakshmikanthanp.clipbird.paring.PairingCoordinator
import com.srilakshmikanthanp.clipbird.paring.PairingFailedException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PairingViewModel(
  private val coordinator: PairingCoordinator,
  private val pairedDeviceService: PairedDeviceService<out PairedDevice>,
) : ViewModel() {
  sealed interface PairingEvent {
    data class AlreadyPairing(val deviceName: String) : PairingEvent
    data class Failed(val deviceName: String) : PairingEvent
    data class Unsupported(val deviceName: String) : PairingEvent
    data class Error(val deviceName: String, val cause: Throwable) : PairingEvent
  }

  data class PairingUiState(
    val discovered: List<DiscoveredUiDevice> = emptyList(),
    val paired: List<PairedDevice> = emptyList(),
  )

  data class DiscoveredUiDevice(
    val candidate: PairingCandidate,
    val isPairing: Boolean,
  )

  val uiState: StateFlow<PairingUiState> = combine(
    coordinator.devices,
    coordinator.pairing,
    pairedDeviceService.getAll(),
  ) { devices, pairing, paired ->
    PairingUiState(
      discovered = devices.map { DiscoveredUiDevice(it, isPairing = it in pairing) },
      paired = paired,
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    PairingUiState()
  )

  private val _events = Channel<PairingEvent>(Channel.BUFFERED)
  val events: Flow<PairingEvent> = _events.receiveAsFlow()

  private fun Throwable.toEvent(candidate: PairingCandidate): PairingEvent = when (this) {
    is AlreadyPairingException -> PairingEvent.AlreadyPairing(candidate.name)
    is IllegalPairingCandidateException -> PairingEvent.Unsupported(candidate.name)
    is PairingFailedException -> PairingEvent.Failed(candidate.name)
    else -> PairingEvent.Error(candidate.name, this)
  }

  fun pair(candidate: PairingCandidate) = viewModelScope.launch {
    runCatching { coordinator.pair(candidate) }.onFailure { _events.send(it.toEvent(candidate)) }
  }

  fun unpair(id: Long) = viewModelScope.launch {
    pairedDeviceService.delete(id)
  }
}
