package com.srilakshmikanthanp.clipbird.ffi.discoverer.ble

interface BleDiscovererListener {
  fun onDiscoveryStarted()
  fun onDeviceDiscovered(deviceId: Long)
  fun onDiscoveryFailed(code: Int, reason: String)
  fun onDiscoveryStopped()
}
