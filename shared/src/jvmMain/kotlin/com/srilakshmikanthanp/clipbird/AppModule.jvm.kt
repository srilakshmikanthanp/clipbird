package com.srilakshmikanthanp.clipbird

import com.srilakshmikanthanp.clipbird.common.CoreModule
import com.srilakshmikanthanp.clipbird.database.DatabaseModule
import com.srilakshmikanthanp.clipbird.database.DatabasePlatformModule
import com.srilakshmikanthanp.clipbird.ffi.loader.NativeClipbirdLoader
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothPlatformModule
import com.srilakshmikanthanp.clipbird.paring.PairingModule
import com.srilakshmikanthanp.clipbird.ui.pairing.PairingViewModelModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
  NativeClipbirdLoader.load()
  startKoin {
    modules(
      CoreModule().module,
      DatabaseModule().module,
      DatabasePlatformModule().module,
      PairingModule().module,
      PairingViewModelModule().module,
      BluetoothPlatformModule().module,
    )
  }
}
