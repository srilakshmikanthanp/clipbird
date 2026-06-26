package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.ffi.BleAdvertiserFfi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
actual class BleAdvertiser(private val serviceUuid: Uuid, private val device: BleHubDevice) : Advertiser<BleHubDevice> {
  private val _advertisedDevice: MutableStateFlow<BleHubDevice?> = MutableStateFlow(null)
  actual override val advertisedDevice = _advertisedDevice.asStateFlow()

  private var bleAdvertiserFfi: BleAdvertiserFfi? = null

  actual override suspend fun startAdvertising() {
    if (bleAdvertiserFfi != null) {
      throw IllegalStateException("Advertiser already started")
    }

    val bleAdvertiserFfi = BleAdvertiserFfi(serviceUuid, ProtoBuf.encodeToByteArray(device))

    runCatching {
      bleAdvertiserFfi.start()
    }.onFailure {
      bleAdvertiserFfi.close()
    }.onSuccess {
      this.bleAdvertiserFfi = bleAdvertiserFfi
      _advertisedDevice.value = device
    }.getOrThrow()
  }

  actual override suspend fun stopAdvertising() {
    val ffi = bleAdvertiserFfi ?: throw IllegalStateException("Not advertising")

    try {
      ffi.stop()
    } finally {
      ffi.close()
      bleAdvertiserFfi = null
      _advertisedDevice.value = null
    }
  }
}
