package com.srilakshmikanthanp.clipbird.utility

class ByteArrayComparator : Comparator<ByteArray> {
  override fun compare(one: ByteArray, two: ByteArray): Int {
    val minLength = minOf(one.size, two.size)
    for (i in 0 until minLength) {
      val diff = one[i].compareTo(two[i])
      if (diff != 0) {
        return diff
      }
    }
    return one.size.compareTo(two.size)
  }
}
