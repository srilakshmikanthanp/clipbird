package com.srilakshmikanthanp.clipbird.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.utility.certificateFingerprint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AboutViewModel(
  private val hostDeviceProvider: HostDeviceProvider,
) : ViewModel() {
  private val _fingerprint = MutableStateFlow<String?>(null)
  val fingerprint: StateFlow<String?> = _fingerprint

  private val _deviceId = MutableStateFlow<String?>(null)
  val deviceId: StateFlow<String?> = _deviceId

  init {
    viewModelScope.launch {
      val device = hostDeviceProvider.get()
      _fingerprint.value = certificateFingerprint(device.certificate)
      _deviceId.value = device.id.toString()
    }
  }
}