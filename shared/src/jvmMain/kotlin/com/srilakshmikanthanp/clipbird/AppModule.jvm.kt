package com.srilakshmikanthanp.clipbird

import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.common.CoreModule
import com.srilakshmikanthanp.clipbird.database.DatabaseModule
import com.srilakshmikanthanp.clipbird.database.DatabasePlatformModule
import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.loader.NativeClipbirdLoader
import com.srilakshmikanthanp.clipbird.ffi.log.LogHandle
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleAdvertiserModule
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothPlatformModule
import com.srilakshmikanthanp.clipbird.paring.PairingModule
import com.srilakshmikanthanp.clipbird.peer.PeerModule
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientModule
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerModule
import com.srilakshmikanthanp.clipbird.ui.device.DeviceModule
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
  val nativeLogger = Logger.withTag("NativeClipbird")

  NativeClipbirdLoader.load()

  LogHandle.setCallback { level, message ->
    when (level) {
      Clipbird.CLIPBIRD_LOG_LEVEL_TRACE() -> nativeLogger.v { message }
      Clipbird.CLIPBIRD_LOG_LEVEL_DEBUG() -> nativeLogger.d { message }
      Clipbird.CLIPBIRD_LOG_LEVEL_INFO() -> nativeLogger.i { message }
      Clipbird.CLIPBIRD_LOG_LEVEL_WARNING() -> nativeLogger.w { message }
      Clipbird.CLIPBIRD_LOG_LEVEL_ERROR() -> nativeLogger.e { message }
      Clipbird.CLIPBIRD_LOG_LEVEL_FATAL() -> nativeLogger.e { message }
    }
  }

  startKoin {
    modules(
      CoreModule().module,
      DatabaseModule().module,
      DatabasePlatformModule().module,
      PairingModule().module,
      DeviceModule().module,
      BluetoothPlatformModule().module,
      BleAdvertiserModule().module,
      ClipbirdServerModule().module,
      ClipbirdClientModule().module,
      PeerModule().module,
      AppModule().module
    )
  }
}
