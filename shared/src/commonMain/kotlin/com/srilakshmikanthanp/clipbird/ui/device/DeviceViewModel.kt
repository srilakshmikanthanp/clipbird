package com.srilakshmikanthanp.clipbird.ui.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.pairing.*
import com.srilakshmikanthanp.clipbird.peer.PeerHub
import com.srilakshmikanthanp.clipbird.ui.device.DeviceViewModel.PairingEvent.*
import kotlinx.coroutines.channels.Channel as KChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class DeviceViewModel<PC : PairingCandidate, D : PairedDevice, C: Channel>(
  private val pairedDeviceService: PairedDeviceService<D>,
  private val coordinator: PairingService<PC, D, C>,
  peerHub: PeerHub<D>,
) : ViewModel() {
  sealed interface PairingEvent {
    data class AlreadyPairing(val deviceName: String) : PairingEvent
    data class Failed(val deviceName: String) : PairingEvent
    data class Unsupported(val deviceName: String) : PairingEvent
    data class Error(val deviceName: String, val cause: Throwable) : PairingEvent
  }

  data class DiscoveredDevice<C: PairingCandidate>(
    val candidate: C,
    val isPairing: Boolean,
  )

  data class Device<D: PairedDevice>(
    val pairedDevice: D,
    val connected: Boolean
  )

  data class DeviceState<C: PairingCandidate, D: PairedDevice>(
    val discovered: List<DiscoveredDevice<C>> = emptyList(),
    val devices: List<Device<D>> = emptyList(),
  )

  val uiState: StateFlow<DeviceState<PC, D>> = combine(
    pairedDeviceService.getAll(),
    coordinator.devices,
    coordinator.pairing,
    peerHub.devices,
  ) { paired, devices, pairing, connected ->
    val connectedIds = connected.asSequence().map { it.id }.toHashSet()

    DeviceState(
      discovered = devices.map { DiscoveredDevice(it, isPairing = it in pairing) },
      devices = paired.map { Device(pairedDevice = it, connected = it.id in connectedIds ) }
    )
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    DeviceState()
  )

  private val _events = KChannel<PairingEvent>(KChannel.BUFFERED)
  val events: Flow<PairingEvent> = _events.receiveAsFlow()

  private fun Throwable.toEvent(candidate: PairingCandidate): PairingEvent = when (this) {
    is AlreadyPairingException -> AlreadyPairing(candidate.name)
    is IllegalPairingCandidateException -> Unsupported(candidate.name)
    is PairingFailedException -> Failed(candidate.name)
    else -> Error(candidate.name, this)
  }

  fun pair(candidate: PC) = viewModelScope.launch {
    runCatching { coordinator.pair(candidate) }.onFailure { _events.send(it.toEvent(candidate)) }
  }

  fun unpair(id: ULong) = viewModelScope.launch {
    pairedDeviceService.delete(id)
  }
}
