package com.srilakshmikanthanp.clipbird.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.utility.publicKeyFingerprint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AboutViewModel(
  private val hostDeviceProvider: HostDeviceProvider,
) : ViewModel() {
  private val _fingerprint = MutableStateFlow("")
  val fingerprint: StateFlow<String> = _fingerprint

  init {
    viewModelScope.launch {
      _fingerprint.value = publicKeyFingerprint(hostDeviceProvider.get().publicKey)
    }
  }
}