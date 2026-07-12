package com.srilakshmikanthanp.clipbird.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.security.KeyPairGenerator
import java.security.SecureRandom

class HostDeviceProvider(private val dao: HostDeviceDao, private val name: String) {
  private var cached: HostDevice? = null
  private val mutex = Mutex()

  private suspend fun create(): HostDeviceEntity {
    val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    val id = SecureRandom().nextLong()
    val device = HostDeviceEntity(id = id, name = name, publicKey = keyPair.public.encoded, privateKey = keyPair.private.encoded)
    dao.upsert(device)
    return device
  }

  suspend fun get(): HostDevice = mutex.withLock {
    cached ?: (dao.get() ?: create()).toHostDevice().also { cached = it }
  }
}
