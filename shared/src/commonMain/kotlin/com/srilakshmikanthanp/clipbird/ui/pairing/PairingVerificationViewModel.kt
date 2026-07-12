package com.srilakshmikanthanp.clipbird.ui.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.paring.BlockingPairingVerifier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PairingVerificationViewModel(private val verifier: BlockingPairingVerifier) : ViewModel() {
  val requests: StateFlow<List<BlockingPairingVerifier.VerificationRequest>> = verifier.requests.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    emptyList()
  )

  fun confirm(request: BlockingPairingVerifier.VerificationRequest) {
    verifier.confirm(request)
  }

  fun reject(request: BlockingPairingVerifier.VerificationRequest) {
    verifier.reject(request)
  }
}
