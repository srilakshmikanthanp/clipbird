package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothChannel
import kotlinx.coroutines.flow.Flow

interface PairingServer<T: Channel> {
  val channels: Flow<T>
}
