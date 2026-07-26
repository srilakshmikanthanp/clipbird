package com.srilakshmikanthanp.clipbird.utility

object ByteArrayComparator : Comparator<ByteArray> {
  override fun compare(one: ByteArray, two: ByteArray): Int {
    val minLength = minOf(one.size, two.size)
    for (i in 0 until minLength) {
      val diff = (one[i].toInt() and 0xFF).compareTo(two[i].toInt() and 0xFF)
      if (diff != 0) {
        return diff
      }
    }
    return one.size.compareTo(two.size)
  }
}
