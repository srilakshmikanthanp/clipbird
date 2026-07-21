package com.srilakshmikanthanp.clipbird.ffi.log

object LogHandle {
  fun setLogCallback(callback: (Int, String) -> Unit) {
    NativeLogBridge.setCallback(callback)
  }

  fun clearLogCallback() {
    NativeLogBridge.clearCallback()
  }
}
