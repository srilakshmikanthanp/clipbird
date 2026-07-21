package com.srilakshmikanthanp.clipbird.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.paring.*
import com.srilakshmikanthanp.clipbird.ui.device.DeviceViewModel.PairingEvent.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeviceViewModel(
  private val pairedDeviceService: PairedDeviceService<out PairedDevice>,
  private val coordinator: PairingCoordinator,
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
      devices = paired.map { Device(pairedDevice = it, connected = false) }
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    DeviceState()
  )

  private val _events = Channel<PairingEvent>(Channel.BUFFERED)
  val events: Flow<PairingEvent> = _events.receiveAsFlow()

  private fun Throwable.toEvent(candidate: PairingCandidate): PairingEvent = when (this) {
    is AlreadyPairingException -> AlreadyPairing(candidate.name)
    is IllegalPairingCandidateException -> Unsupported(candidate.name)
    is PairingFailedException -> Failed(candidate.name)
    else -> Error(candidate.name, this)
  }

  fun pair(candidate: PairingCandidate) = viewModelScope.launch {
    runCatching { coordinator.pair(candidate) }.onFailure { _events.send(it.toEvent(candidate)) }
  }

  fun unpair(id: Long) = viewModelScope.launch {
    pairedDeviceService.delete(id)
  }
}
