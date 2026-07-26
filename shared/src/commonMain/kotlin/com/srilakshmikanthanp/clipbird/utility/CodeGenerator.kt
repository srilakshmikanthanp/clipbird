package com.srilakshmikanthanp.clipbird.utility

import java.security.MessageDigest

object CodeGenerator {
  fun generate(vararg inputs: ByteArray, digits: Int = 8): String {
    val sortedInputs = inputs.sortedWith(ByteArrayComparator)
    val combined = sortedInputs.reduce { acc, bytes -> acc + bytes }
    val hash = MessageDigest.getInstance("SHA-256").digest(combined)
    return hash.joinToString("") { "%02X".format(it) }.take(digits)
  }
}
