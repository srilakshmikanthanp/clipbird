package com.srilakshmikanthanp.clipbird.paring

interface PairingCodeGenerator {
  fun generate(one: ByteArray, two: ByteArray): String
}
