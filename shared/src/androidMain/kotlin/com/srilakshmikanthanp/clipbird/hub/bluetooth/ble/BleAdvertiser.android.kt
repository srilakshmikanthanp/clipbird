package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import android.Manifest.permission
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)
actual class BleAdvertiser(private val context: Context, private val device: BleHubDevice) : Advertiser<BleHubDevice> {
  private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
  private var advertiseCallback: AdvertiseCallback? = null

  private val _advertisedDevice: MutableStateFlow<BleHubDevice?> = MutableStateFlow(null)
  actual override val advertisedDevice = _advertisedDevice.asStateFlow()

  @RequiresPermission(permission.BLUETOOTH_ADVERTISE)
  actual override suspend fun startAdvertising() {
    if (_advertisedDevice.value != null) {
      throw AdvertisingException("Already advertising")
    }

    val adapter = bluetoothManager.adapter ?: throw AdvertisingException("BLE adapter not available")

    if (!adapter.isEnabled) {
      throw AdvertisingException("Bluetooth is disabled")
    }

    if (!adapter.isMultipleAdvertisementSupported) {
      throw AdvertisingException("BLE advertising is not supported on this device")
    }

    val advertiser = adapter.bluetoothLeAdvertiser ?: throw AdvertisingException("BLE advertiser not available")

    val serviceUuid = ParcelUuid(BluetoothConstants.clipbirdServiceUuid.toJavaUuid())
    val serviceData = ProtoBuf.encodeToByteArray(device)

    val settings = AdvertiseSettings.Builder()
      .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
      .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
      .setConnectable(false)
      .setTimeout(0)
      .build()

    val advertiseData = AdvertiseData.Builder()
      .addServiceUuid(serviceUuid)
      .setIncludeDeviceName(false)
      .setIncludeTxPowerLevel(false)
      .build()

    val scanResponse = AdvertiseData.Builder()
      .addServiceData(serviceUuid, serviceData)
      .build()

    suspendCancellableCoroutine { continuation ->
      val callback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
          advertiseCallback = this
          _advertisedDevice.value = device

          if (continuation.isActive) {
            continuation.resume(Unit)
          }
        }

        override fun onStartFailure(errorCode: Int) {
          if (continuation.isActive) {
            continuation.resumeWithException(
              AdvertisingException(
                "Failed to start advertising. errorCode=$errorCode"
              )
            )
          }
        }
      }

      advertiser.startAdvertising(
        settings,
        advertiseData,
        scanResponse,
        callback,
      )

      continuation.invokeOnCancellation {
        advertiser.stopAdvertising(callback)
      }
    }
  }

  @RequiresPermission(permission.BLUETOOTH_ADVERTISE)
  actual override suspend fun stopAdvertising() {
    val adapter = bluetoothManager.adapter ?: throw AdvertisingException("BLE adapter not available")
    val advertiser = adapter.bluetoothLeAdvertiser ?: throw AdvertisingException("BLE advertiser not available")

    advertiseCallback?.let {
      advertiser.stopAdvertising(it)
    }

    advertiseCallback = null
    _advertisedDevice.value = null
  }
}