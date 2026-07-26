package com.srilakshmikanthanp.clipbird.ui.history

import com.srilakshmikanthanp.clipbird.clipboard.Clipboard
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardHistory
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class HistoryModule {
  @KoinViewModel
  fun historyViewModel(
    clipboardHistory: ClipboardHistory,
    clipboard: Clipboard,
    peerHub: BluetoothPeerHub,
  ): HistoryViewModel = HistoryViewModel(
    clipboardHistory,
    clipboard,
    peerHub
  )
}
