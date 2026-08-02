package com.srilakshmikanthanp.clipbird.clipboard.replication.bluetooth

import com.srilakshmikanthanp.clipbird.clipboard.Clipboard
import com.srilakshmikanthanp.clipbird.clipboard.replication.ClipboardReplicator
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardHistory
import com.srilakshmikanthanp.clipbird.pairing.bluetooth.BluetoothPairedDevice
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.CoroutineScope

class BluetoothClipboardReplicator(
  peerHub: BluetoothPeerHub,
  clipboard: Clipboard,
  clipboardHistory: ClipboardHistory,
  scope: CoroutineScope,
): ClipboardReplicator<BluetoothPairedDevice>(
  peerHub,
  clipboard,
  clipboardHistory,
  scope,
)
