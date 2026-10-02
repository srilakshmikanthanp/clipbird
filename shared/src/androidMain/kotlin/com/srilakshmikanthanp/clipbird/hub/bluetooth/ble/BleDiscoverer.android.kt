package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import android.Manifest.permission
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import co.touchlab.kermit.Logger
import com.srilakshmikanthanp.clipbird.hub.Discoverer
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Found
import com.srilakshmikanthanp.clipbird.hub.DiscoveryEvent.Lost
import com.srilakshmikanthanp.clipbird.hub.DiscoveryException
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer.Message.CleanUp
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer.Message.DeviceFound
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
@SuppressLint("MissingPermission")
actual class BleDiscoverer(
  private val context: Context,
  private val serviceUuid: Uuid,
  private val deviceTimeout: Duration,
) : Discoverer<BleHubDevice> {
  private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)

  @RequiresPermission(permission.BLUETOOTH_SCAN)
  actual override val events: Flow<DiscoveryEvent<BleHubDevice>> = channelFlow {
    val devices = mutableMapOf<ULong, SeenDevice>()
    val channel = Channel<Message>(64)

    val handleDeviceFound = suspend { device: BleHubDevice ->
      Logger.i (tag = TAG) { "Found device via Discovery: $device" }
      val now = System.currentTimeMillis()
      if (device.id !in devices) send(Found(device))
      devices[device.id] = SeenDevice(device, now)
    }

    val handleCleanUp = suspend {
      val cutoff = System.currentTimeMillis() - deviceTimeout.inWholeMilliseconds
      val lost = devices.values.filter { it.lastSeen < cutoff }.map { it.device }

      lost.forEach { device ->
        Logger.i (tag = TAG) { "Device lost via Discovery: $device" }
        devices.remove(device.id)
        send(Lost(device))
      }
    }

    val adapter = bluetoothManager.adapter ?: throw DiscoveryException("BLE adapter not available")

    if (!adapter.isEnabled) {
      throw DiscoveryException("Bluetooth is disabled")
    }

    val scanner = adapter.bluetoothLeScanner ?: throw DiscoveryException("BLE scanner not available")

    val javaUuid = serviceUuid.toJavaUuid()

    val uuidPrefix = ByteBuffer.allocate(24)
      .putLong(javaUuid.mostSignificantBits)
      .putLong(javaUuid.leastSignificantBits)
      .putLong(0L)
      .array()

    val uuidMask = ByteBuffer.allocate(24)
      .putLong(-1L)
      .putLong(-1L)
      .putLong(0L)
      .array()

    val scanFilter = ScanFilter.Builder()
      .setManufacturerData(0xFFFF, uuidPrefix, uuidMask)
      .build()

    val scanSettings = ScanSettings.Builder()
      .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
      .build()

    val scanCallback = object : ScanCallback() {
      override fun onScanResult(callbackType: Int, result: ScanResult) {
        result.toDevice()?.let { channel.trySend(DeviceFound(it)) }
      }

      override fun onScanFailed(errorCode: Int) {
        close(DiscoveryException("BLE discovery failed (errorCode=$errorCode)"))
      }
    }

    val bluetoothStateReceiver = object : BroadcastReceiver() {
      override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action != BluetoothAdapter.ACTION_STATE_CHANGED) return
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        if (state == BluetoothAdapter.STATE_TURNING_OFF || state == BluetoothAdapter.STATE_OFF) {
          close(DiscoveryException("Bluetooth was turned off during discovery"))
        }
      }
    }

    context.registerReceiver(
      bluetoothStateReceiver,
      IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    )

    try {
      scanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
    } catch (e: Exception) {
      context.unregisterReceiver(bluetoothStateReceiver)
      throw DiscoveryException("Failed to start BLE discovery: ${e.message}", e)
    }

    val cleaningJob = launch {
      while (true) {
        channel.send(CleanUp)
        delay(1000.milliseconds)
      }
    }

    val processingJob = launch {
      for (message in channel) {
        when (message) {
          is DeviceFound -> handleDeviceFound(message.device)
          is CleanUp -> handleCleanUp()
        }
      }
    }

    awaitClose {
      context.unregisterReceiver(bluetoothStateReceiver)
      runCatching { scanner.stopScan(scanCallback) }
      cleaningJob.cancel()
      processingJob.cancel()
    }
  }

  private fun ScanResult.toDevice(): BleHubDevice? {
    val data = this.scanRecord?.getManufacturerSpecificData(0xFFFF) ?: return null
    val uuid = serviceUuid.toJavaUuid()

    if (data.size != 24) return null

    val buf = ByteBuffer.wrap(data)
    val msb = buf.getLong()
    val lsb = buf.getLong()
    val id = buf.getLong().toULong()

    if (msb != uuid.mostSignificantBits || lsb != uuid.leastSignificantBits) return null

    return BleHubDevice(id)
  }

  private data class SeenDevice(val device: BleHubDevice, val lastSeen: Long)

  internal sealed interface Message {
    data class DeviceFound(val device: BleHubDevice) : Message
    data object CleanUp : Message
  }

  companion object {
    private const val TAG = "BleDiscoverer.android"
  }
}
