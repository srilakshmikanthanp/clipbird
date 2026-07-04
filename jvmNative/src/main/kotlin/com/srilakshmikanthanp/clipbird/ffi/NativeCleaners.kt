package com.srilakshmikanthanp.clipbird.ffi

import java.lang.ref.Cleaner

object NativeCleaners {
  val cleaner: Cleaner = Cleaner.create()
}
