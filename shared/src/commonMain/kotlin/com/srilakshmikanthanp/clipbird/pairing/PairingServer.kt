package com.srilakshmikanthanp.clipbird.pairing

import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.flow.Flow

interface PairingServer<T: Channel> {
  val channels: Flow<T>
}
