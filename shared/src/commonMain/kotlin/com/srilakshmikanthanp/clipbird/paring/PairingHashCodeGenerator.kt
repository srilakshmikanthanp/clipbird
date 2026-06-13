package com.srilakshmikanthanp.clipbird.paring

import java.security.MessageDigest

class PairingHashCodeGenerator : PairingCodeGenerator {
  private fun sort(one: ByteArray, two: ByteArray): Pair<ByteArray, ByteArray> {
    return if (compare(one, two) <= 0) {
      Pair(one, two)
    } else {
      Pair(two, one)
    }
  }

  private fun compare(one: ByteArray, two: ByteArray): Int {
    val minLength = minOf(one.size, two.size)
    for (i in 0 until minLength) {
      val diff = one[i].compareTo(two[i])
      if (diff != 0) {
        return diff
      }
    }
    return one.size.compareTo(two.size)
  }

  override fun generate(one: ByteArray, two: ByteArray): String {
    val (sortedOne, sortedTwo) = sort(one, two)
    val combined = sortedOne + sortedTwo
    val hash = MessageDigest.getInstance("SHA-256").digest(combined)
    return hash.joinToString("") { "%02X".format(it) }.take(8)
  }
}
