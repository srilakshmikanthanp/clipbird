package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Advertiser
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

  private val bleAdvertiserFfi: BleAdvertiserFfi = BleAdvertiserFfi(serviceUuid, ProtoBuf.encodeToByteArray(device))

  actual override suspend fun startAdvertising() {
    if (_advertisedDevice.value != null) {
      throw IllegalStateException("Already advertising")
    }

    bleAdvertiserFfi.start()
    _advertisedDevice.value = device
  }

  actual override suspend fun stopAdvertising() {
    if (_advertisedDevice.value == null) {
      throw IllegalStateException("Not currently advertising")
    }

    bleAdvertiserFfi.stop()
    _advertisedDevice.value = null
  }
}
