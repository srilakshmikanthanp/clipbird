package com.srilakshmikanthanp.clipbird.io.bluetooth

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/** Desktop Bluetooth binding. */
@Module
class BluetoothPlatformModule {
  @Single
  fun bluetoothManager(): BluetoothManager = BluetoothManager()
}
