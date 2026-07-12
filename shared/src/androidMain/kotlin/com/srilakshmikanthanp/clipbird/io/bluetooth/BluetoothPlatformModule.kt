package com.srilakshmikanthanp.clipbird.io.bluetooth

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/** Android Bluetooth binding. `Context` resolves from the `androidContext()` registration. */
@Module
class BluetoothPlatformModule {
  @Single
  fun bluetoothManager(scope: CoroutineScope, context: Context): BluetoothManager =
    BluetoothManager(scope, context)
}
