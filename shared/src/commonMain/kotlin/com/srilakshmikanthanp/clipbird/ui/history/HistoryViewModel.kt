package com.srilakshmikanthanp.clipbird.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srilakshmikanthanp.clipbird.clipboard.Clipboard
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardHistory
import com.srilakshmikanthanp.clipbird.peer.BluetoothPeerHub
import com.srilakshmikanthanp.clipbird.peer.TransferState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
  private val clipboardHistory: ClipboardHistory,
  private val clipboard: Clipboard,
  private val peerHub: BluetoothPeerHub,
) : ViewModel() {
  val history: StateFlow<List<ClipboardContent>> = clipboardHistory.history.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5_000),
    emptyList(),
  )

  val transferState: StateFlow<TransferState> = peerHub.transferState

  fun sendClipboard() = viewModelScope.launch {
    runCatching { peerHub.sendClipboard(clipboard.get()) }
  }

  fun copyToClipboard(index: Int) = viewModelScope.launch {
    clipboardHistory.history.value.getOrNull(index)?.let { clipboard.set(it) }
  }

  fun deleteAt(index: Int) = viewModelScope.launch {
    clipboardHistory.deleteAt(index)
  }
}
