package com.srilakshmikanthanp.clipbird.ui.trust

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.pairing.PairedDeviceService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrustViewModel(
  private val pairedDeviceService: PairedDeviceService<*>,
) : ViewModel() {
  val devices: StateFlow<List<PairedDevice>> = pairedDeviceService.getAll().stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    emptyList(),
  )

  fun remove(id: Long) = viewModelScope.launch {
    pairedDeviceService.delete(id)
  }
}