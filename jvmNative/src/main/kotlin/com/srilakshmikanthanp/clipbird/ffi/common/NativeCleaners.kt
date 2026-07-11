package com.srilakshmikanthanp.clipbird.ffi.common

import java.lang.ref.Cleaner

object NativeCleaners {
  val cleaner: Cleaner = Cleaner.create()
}