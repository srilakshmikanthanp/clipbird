package com.srilakshmikanthanp.clipbird.peer.server

import com.srilakshmikanthanp.clipbird.io.Channel
import kotlinx.coroutines.flow.Flow

interface ClipbirdServer {
  val channels: Flow<Channel>
}
