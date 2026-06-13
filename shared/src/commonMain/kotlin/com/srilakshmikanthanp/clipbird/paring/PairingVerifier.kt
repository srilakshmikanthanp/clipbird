package com.srilakshmikanthanp.clipbird.paring

import com.srilakshmikanthanp.clipbird.common.Device

interface PairingVerifier {
  suspend fun verify(localDevice: Device, remoteDevice: Device, code: String): Boolean
}
