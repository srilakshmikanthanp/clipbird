package com.srilakshmikanthanp.clipbird.peer.authentication

import com.srilakshmikanthanp.clipbird.utility.Nonce

class ExchangedNonce(
  val local: ByteArray,
  val remote: ByteArray,
)
