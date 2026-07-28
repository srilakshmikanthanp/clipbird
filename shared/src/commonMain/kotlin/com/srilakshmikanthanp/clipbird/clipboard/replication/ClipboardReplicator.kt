package com.srilakshmikanthanp.clipbird.clipboard.replication

import com.srilakshmikanthanp.clipbird.clipboard.Clipboard
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardHistory
import com.srilakshmikanthanp.clipbird.pairing.PairedDevice
import com.srilakshmikanthanp.clipbird.peer.PeerHub
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

open class ClipboardReplicator<P: PairedDevice>(
  private val peerHub: PeerHub<P>,
  private val clipboard: Clipboard,
  private val clipboardHistory: ClipboardHistory
) {
  private suspend fun syncClipboardFromHistory() {
    clipboardHistory.latest.collect {
      clipboard.set(it)
    }
  }

  private suspend fun syncHistoryFromPeers() {
    peerHub.clipboard.collect {
      clipboardHistory.push(it)
    }
  }

  private suspend fun syncClipboardToPeers() {
    clipboard.data.collect {
      peerHub.sendClipboard(it)
    }
  }

  suspend fun run() = coroutineScope {
    launch { syncClipboardFromHistory() }
    launch { syncHistoryFromPeers() }
    launch { syncClipboardToPeers() }
  }
}
