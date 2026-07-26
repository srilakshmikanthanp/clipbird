package com.srilakshmikanthanp.clipbird.clipboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ClipboardHistory {
  private val _history = MutableStateFlow(emptyList<ClipboardContent>())
  val history: StateFlow<List<ClipboardContent>> = _history.asStateFlow()
  private val deque = ArrayDeque<ClipboardContent>()
  private val _latest = MutableStateFlow(ClipboardContent.Empty)
  val latest = _latest.asStateFlow()
  private val mutex = Mutex()

  suspend fun push(content: ClipboardContent) = mutex.withLock {
    deque.addFirst(content)
    if (deque.size > MAX_HISTORY) deque.removeLast()
    _history.value = deque.toList()
    _latest.value = content
  }

  suspend fun deleteAt(index: Int) = mutex.withLock {
    if (index < 0 || index >= deque.size) return@withLock
    deque.removeAt(index)
    _history.value = deque.toList()
  }

  companion object {
    const val MAX_HISTORY = 10
  }
}
