package com.srilakshmikanthanp.clipbird.handlers

import androidx.activity.ComponentActivity
import com.srilakshmikanthanp.clipbird.clipboard.Clipboard
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class SendHandler : ComponentActivity() {
  private val applicationScope: CoroutineScope by inject()
  private val clipboard: Clipboard by inject()
  private val peerHub: BluetoothPeerHub by inject()

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (!hasFocus) return
    val content = runBlocking { clipboard.get() }
    applicationScope.launch { peerHub.sendClipboard(content) }
    finish()
  }
}
