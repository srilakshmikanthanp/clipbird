package com.srilakshmikanthanp.clipbird.common

import com.srilakshmikanthanp.clipbird.crypto.SelfSignedCertificates
import com.srilakshmikanthanp.clipbird.crypto.KeyPairs
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.SecureRandom

class HostDeviceProvider(private val dao: HostDeviceDao, private val name: String) {
  private var cached: HostDevice? = null
  private val mutex = Mutex()

  private suspend fun create(): HostDeviceEntity {
    val keyPair = KeyPairs.generate()
    val id = SecureRandom().nextLong().toULong()
    val certificate = SelfSignedCertificates.create(keyPair, id.toString())
    val device = HostDeviceEntity(id = id.toLong(), name = name, certificate = certificate.encoded, privateKey = keyPair.private.encoded)
    dao.upsert(device)
    return device
  }

  suspend fun get(): HostDevice = mutex.withLock {
    cached ?: (dao.get() ?: create()).toHostDevice().also { cached = it }
  }
}
