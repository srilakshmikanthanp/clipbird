package com.srilakshmikanthanp.clipbird.ffi.advertiser.ble

interface BleAdvertiserListener {
  fun onAdvertisingStarted()
  fun onAdvertisingFailed(code: Int, reason: String)
  fun onAdvertisingStopped()
}
