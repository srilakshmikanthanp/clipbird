package com.srilakshmikanthanp.clipbird.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec

object KeyPairs {
  const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
  const val CURVE = "secp256r1"
  const val KEY_ALGORITHM = "EC"

  fun generate(): KeyPair = KeyPairGenerator.getInstance(KEY_ALGORITHM).apply {
    initialize(ECGenParameterSpec(CURVE))
  }.generateKeyPair()
}
