package com.srilakshmikanthanp.clipbird.pairing

import com.srilakshmikanthanp.clipbird.pairing.PairingDeferredVerifier
import com.srilakshmikanthanp.clipbird.pairing.PairingDeferredVerifier.VerificationRequest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class PairingDialogManager : KoinComponent {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  private val verifier: PairingDeferredVerifier by inject()

  private suspend fun process() =verifier.requests.filterNotNull().collect { request ->
    val deferred = CompletableDeferred<Int>()
    SwingUtilities.invokeLater { deferred.complete(showDialog(request)) }
    if (deferred.await() == JOptionPane.YES_OPTION) {
      verifier.confirm(request)
    } else {
      verifier.reject(request)
    }
  }

  private fun showDialog(request: VerificationRequest): Int {
    return JOptionPane.showOptionDialog(
      null,
      "${request.remoteDevice.name} wants to pair with this device.\n\nVerification code: ${request.code}",
      "Pairing Request",
      JOptionPane.YES_NO_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      arrayOf("Confirm", "Reject"),
      "Confirm",
    )
  }

  fun start() {
    scope.launch { process() }
  }

  fun stop() {
    scope.cancel()
  }
}
