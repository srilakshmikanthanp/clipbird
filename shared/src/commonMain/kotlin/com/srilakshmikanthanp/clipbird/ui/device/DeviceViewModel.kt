package com.srilakshmikanthanp.clipbird.ui.device

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

class DeviceViewModel(
  private val coordinator: PairingCoordinator,
  private val pairedDeviceService: PairedDeviceService<out PairedDevice>,
) : ViewModel() {
  sealed interface PairingEvent {
    data class AlreadyPairing(val deviceName: String) : PairingEvent
    data class Failed(val deviceName: String) : PairingEvent
    data class Unsupported(val deviceName: String) : PairingEvent
    data class Error(val deviceName: String, val cause: Throwable) : PairingEvent
  }

  data class DiscoveredDevice(
    val candidate: PairingCandidate,
    val isPairing: Boolean,
  )

  data class Device(
    val pairedDevice: PairedDevice,
    val connected: Boolean
  )

  data class DeviceState(
    val discovered: List<DiscoveredDevice> = emptyList(),
    val devices: List<Device> = emptyList(),
  )

  val uiState: StateFlow<DeviceState> = combine(
    pairedDeviceService.getAll(),
    coordinator.devices,
    coordinator.pairing,
  ) { paired, devices, pairing ->
    DeviceState(
      discovered = devices.map { DiscoveredDevice(it, isPairing = it in pairing) },
      devices = paired.map { Device(pairedDevice = it, connected = true) }
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    DeviceState()
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
