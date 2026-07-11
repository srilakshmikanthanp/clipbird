package com.srilakshmikanthanp.clipbird.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.common.NativeCleaners
import com.srilakshmikanthanp.clipbird.ffi.io.bluetooth.BluetoothManagerHandle
import java.lang.foreign.MemorySegment
import kotlin.uuid.ExperimentalUuidApi

class NativeBluetoothManager : AutoCloseable {
  private val memorySegment = BluetoothManagerHandle.create()
  private val cleanable = NativeCleaners.cleaner.register(this) {
    BluetoothManagerHandle.destroy(memorySegment)
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
