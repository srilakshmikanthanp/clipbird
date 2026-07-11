package com.srilakshmikanthanp.clipbird.io.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.srilakshmikanthanp.clipbird.io.Channel
import com.srilakshmikanthanp.clipbird.io.ClientFactory
import com.srilakshmikanthanp.clipbird.io.Server
import com.srilakshmikanthanp.clipbird.io.ServerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import android.bluetooth.BluetoothDevice as AndroidBluetoothDevice
import android.bluetooth.BluetoothManager as AndroidBluetoothManager

@OptIn(ExperimentalUuidApi::class)
@SuppressLint("MissingPermission")
actual class BluetoothManager(
  private val coroutineScope: CoroutineScope,
  private val context: Context
) : ClientFactory<BluetoothServerEndpoint>, ServerFactory<BluetoothServerConfig> {
  private val androidBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as AndroidBluetoothManager
  private val bluetoothAdapter = androidBluetoothManager.adapter

  @OptIn(ExperimentalUuidApi::class)
  private fun AndroidBluetoothDevice.toSharedBluetoothDevice(): BluetoothDevice {
    return BluetoothDevice(address = address, name = name)
  }

  actual val boundedDevices: StateFlow<List<BluetoothDevice>> = callbackFlow {
    val receiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        trySend(bluetoothAdapter.bondedDevices.map { it.toSharedBluetoothDevice() })
      }
    }

    trySend(bluetoothAdapter.bondedDevices.map { it.toSharedBluetoothDevice() })

    context.registerReceiver(receiver, IntentFilter().apply {
      addAction(AndroidBluetoothDevice.ACTION_BOND_STATE_CHANGED)
      addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    })

    awaitClose {
      context.unregisterReceiver(receiver)
    }
  }.stateIn(
    initialValue = bluetoothAdapter.bondedDevices.map { it.toSharedBluetoothDevice() },
    scope = coroutineScope,
    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
  )

  override suspend fun connect(endpoint: BluetoothServerEndpoint): Channel = withContext(Dispatchers.IO) {
    val device = bluetoothAdapter.getRemoteDevice(endpoint.address)
    val uuid = endpoint.serviceUuid.toJavaUuid()
    val socket = device.createRfcommSocketToServiceRecord(uuid)
    bluetoothAdapter.cancelDiscovery()
    socket.connect()
    BluetoothChannel(socket)
  }

  override suspend fun start(config: BluetoothServerConfig): Server = withContext(Dispatchers.IO) {
    val serviceName = config.serviceName
    val uuid = config.serviceUuid.toJavaUuid()
    val bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName, uuid)
    BluetoothServer(bluetoothServerSocket)
  }
}
