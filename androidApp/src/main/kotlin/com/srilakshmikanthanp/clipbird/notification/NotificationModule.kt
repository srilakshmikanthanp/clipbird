package com.srilakshmikanthanp.clipbird.notification

import android.content.Context
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NotificationModule {
  @Single
  fun transferNotificationManager(
    context: Context,
    peerHub: BluetoothPeerHub,
    scope: CoroutineScope,
  ): TransferNotificationManager = TransferNotificationManager(context, peerHub, scope)
}
