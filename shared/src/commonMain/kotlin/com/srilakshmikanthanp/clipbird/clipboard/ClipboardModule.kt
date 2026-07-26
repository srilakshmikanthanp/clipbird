package com.srilakshmikanthanp.clipbird.clipboard

import com.srilakshmikanthanp.clipbird.clipboard.replication.bluetooth.BluetoothClipboardReplicator
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ClipboardModule {
  @Single
  fun clipboardHistory(): ClipboardHistory = ClipboardHistory()

  @Single
  fun replicator(
    peerHub: BluetoothPeerHub,
    clipboard: Clipboard,
    clipboardHistory: ClipboardHistory
  ): BluetoothClipboardReplicator = BluetoothClipboardReplicator(
    peerHub,
    clipboard,
    clipboardHistory
  )
}
