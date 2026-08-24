package com.srilakshmikanthanp.clipbird.pairing

import com.srilakshmikanthanp.clipbird.common.Device
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class PairingDeferredVerifier : PairingVerifier {
  private val pending = MutableStateFlow<Map<VerificationRequest, CompletableDeferred<Boolean>>>(emptyMap())
  val requests: Flow<VerificationRequest?> = pending.map { it.keys.firstOrNull() }.distinctUntilChanged()
  data class VerificationRequest(val remoteDevice: Device, val code: String)

  override suspend fun verify(localDevice: Device, remoteDevice: Device, code: String): Boolean {
    val deferred = CompletableDeferred<Boolean>()
    val request = VerificationRequest(remoteDevice, code)
    pending.update { it + (request to deferred) }

    return try {
      deferred.await()
    } finally {
      pending.update { it - request }
    }
  }

  fun confirm(request: VerificationRequest) {
    pending.value[request]?.complete(true)
  }

  fun reject(request: VerificationRequest) {
    pending.value[request]?.complete(false)
  }

  fun confirmById(deviceId: ULong) {
    pending.value.keys.find { it.remoteDevice.id == deviceId }?.let { confirm(it) }
  }

  fun rejectById(deviceId: ULong) {
    pending.value.keys.find { it.remoteDevice.id == deviceId }?.let { reject(it) }
  }
}
