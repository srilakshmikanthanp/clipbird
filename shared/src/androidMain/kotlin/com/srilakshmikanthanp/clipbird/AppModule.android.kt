package com.srilakshmikanthanp.clipbird

import android.content.Context
import com.srilakshmikanthanp.clipbird.common.CoreModule
import com.srilakshmikanthanp.clipbird.database.DatabaseModule
import com.srilakshmikanthanp.clipbird.database.DatabasePlatformModule
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleAdvertiserModule
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothPlatformModule
import com.srilakshmikanthanp.clipbird.paring.PairingModule
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerModule
import com.srilakshmikanthanp.clipbird.ui.device.DeviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin(context: Context) {
  startKoin {
    androidContext(context)
    modules(
      CoreModule().module,
      DatabaseModule().module,
      DatabasePlatformModule().module,
      PairingModule().module,
      DeviceModule().module,
      BluetoothPlatformModule().module,
      BleAdvertiserModule().module,
      ClipbirdServerModule().module,
    )
  }
}
