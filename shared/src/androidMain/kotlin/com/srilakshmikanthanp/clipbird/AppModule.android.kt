package com.srilakshmikanthanp.clipbird

import android.content.Context
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardModule
import com.srilakshmikanthanp.clipbird.common.CoreModule
import com.srilakshmikanthanp.clipbird.database.DatabaseModule
import com.srilakshmikanthanp.clipbird.database.DatabasePlatformModule
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardPlatformModule
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleAdvertiserModule
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothPlatformModule
import com.srilakshmikanthanp.clipbird.paring.PairingModule
import com.srilakshmikanthanp.clipbird.peer.PeerModule
import com.srilakshmikanthanp.clipbird.peer.client.ClipbirdClientModule
import com.srilakshmikanthanp.clipbird.peer.server.ClipbirdServerModule
import com.srilakshmikanthanp.clipbird.ui.about.AboutModule
import com.srilakshmikanthanp.clipbird.ui.device.DeviceModule
import com.srilakshmikanthanp.clipbird.ui.history.HistoryModule
import com.srilakshmikanthanp.clipbird.ui.trust.TrustModule
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
      HistoryModule().module,
      TrustModule().module,
      AboutModule().module,
      ClipboardModule().module,
      ClipboardPlatformModule().module,
      BluetoothPlatformModule().module,
      BleAdvertiserModule().module,
      ClipbirdServerModule().module,
      ClipbirdClientModule().module,
      PeerModule().module,
      AppModule().module
    )
  }
}
