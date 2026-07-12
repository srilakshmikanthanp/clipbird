package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothManagerHandle
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import kotlin.uuid.ExperimentalUuidApi

class NativeBluetoothManager : AutoCloseable {
  private val memorySegment = BluetoothManagerHandle.create()

  private val arena = Arena.ofShared()

  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothManagerHandle.removeBondedDevicesChangedCallback(memorySegment)
    arena.close()
    BluetoothManagerHandle.destroy(memorySegment)
  }

  fun setBondedDevicesChangedCallback(callback: () -> Unit) {
    BluetoothManagerHandle.setBondedDevicesChangedCallback(memorySegment, arena) { callback() }
  }

  fun removeBondedDevicesChangedCallback() {
    BluetoothManagerHandle.removeBondedDevicesChangedCallback(memorySegment)
  }

  fun bondedDevices(): List<BluetoothDevice> {
    return BluetoothManagerHandle.bondedDevices(memorySegment).use { list ->
      (0L until list.size).map { BluetoothDevice(list.address(it), list.name(it)) }
    }
  }

  fun name(): String {
    return BluetoothManagerHandle.localName(memorySegment)
  }

  @OptIn(ExperimentalUuidApi::class)
  fun connect(endpoint: BluetoothServerEndpoint): MemorySegment {
    return BluetoothManagerHandle.connect(memorySegment, endpoint.address, endpoint.serviceUuid.toString())
  }

  @OptIn(ExperimentalUuidApi::class)
  fun start(config: BluetoothServerConfig): MemorySegment {
    return BluetoothManagerHandle.start(memorySegment, config.serviceName, config.serviceUuid.toString())
  }

  override fun close() {
    cleanable.clean()
  }
}
