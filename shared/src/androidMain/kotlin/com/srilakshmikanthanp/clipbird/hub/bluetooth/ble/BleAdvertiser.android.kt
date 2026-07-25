package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import android.Manifest.permission
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import androidx.annotation.RequiresPermission
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.AdvertisingException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
actual class BleAdvertiser(
  private val context: Context,
  private val serviceUuid: Uuid,
  private val hostDeviceProvider: HostDeviceProvider,
) : Advertiser {
  private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)

  @RequiresPermission(permission.BLUETOOTH_ADVERTISE)
  actual override suspend fun advertise() {
    val adapter = bluetoothManager.adapter ?: throw AdvertisingException("BLE adapter not available")
    val device = BleHubDevice(hostDeviceProvider.get().id)

    if (!adapter.isEnabled) {
      throw AdvertisingException("Bluetooth is disabled")
    }

    val bleAdvertiser = adapter.bluetoothLeAdvertiser
      ?: throw AdvertisingException("BLE advertiser not available")

    val javaUuid = serviceUuid.toJavaUuid()

    val manufacturerData = ByteBuffer.allocate(24)
      .putLong(javaUuid.mostSignificantBits)
      .putLong(javaUuid.leastSignificantBits)
      .putLong(device.id)
      .array()

    val settings = AdvertiseSettings.Builder()
      .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
      .setConnectable(false)
      .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
      .build()

    val advertiseData = AdvertiseData.Builder()
      .addManufacturerData(0xFFFF, manufacturerData)
      .setIncludeDeviceName(false)
      .setIncludeTxPowerLevel(false)
      .build()

    val callback = suspendCancellableCoroutine<AdvertiseCallback> { continuation ->
      val callback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
          if (continuation.isActive) {
            continuation.resume(this)
          }
        }

        override fun onStartFailure(errorCode: Int) {
          if (continuation.isActive) {
            continuation.resumeWithException(
              AdvertisingException("Failed to start advertising. errorCode=$errorCode")
            )
          }
        }
      }

      bleAdvertiser.startAdvertising(settings, advertiseData, callback)

      continuation.invokeOnCancellation {
        bleAdvertiser.stopAdvertising(callback)
      }
    }

    try {
      awaitCancellation()
    } finally {
      withContext(NonCancellable) {
        bleAdvertiser.stopAdvertising(callback)
      }
    }
  }
}