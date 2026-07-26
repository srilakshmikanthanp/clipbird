package com.srilakshmikanthanp.clipbird.peer

sealed interface TransferState {
  data class Progress(val current: Int, val total: Int) : TransferState
  data object Success : TransferState
  data class Failure(val error: Throwable? = null) : TransferState
}
