package com.srilakshmikanthanp.clipbird.ffi.io.bluetooth

import com.srilakshmikanthanp.clipbird.ffi.bindings.Clipbird
import com.srilakshmikanthanp.clipbird.ffi.error.NativeErrorHandle
import com.srilakshmikanthanp.clipbird.ffi.extensions.orThrow
import java.io.IOException
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.ConcurrentHashMap
import kotlin.use
import kotlin.uuid.ExperimentalUuidApi

object BluetoothManagerHandle {
  private val callbacks = ConcurrentHashMap<Long, () -> Unit>()

  private val handle = MethodHandles.lookup().findStatic(
    BluetoothManagerHandle::class.java,
    "dispatchCallback",
    MethodType.methodType(Void.TYPE, MemorySegment::class.java)
  )

  private val descriptor = FunctionDescriptor.ofVoid(
    ValueLayout.ADDRESS
  )

  fun setBondedDevicesChangedCallback(manager: MemorySegment, arena: Arena, callback: () -> Unit) {
    val stub = Linker.nativeLinker().upcallStub(handle, descriptor, arena)
    callbacks[manager.address()] = callback
    Clipbird.clipbird_bluetooth_manager_set_bonded_devices_changed_callback(manager, stub, manager)
  }

  fun removeBondedDevicesChangedCallback(manager: MemorySegment) {
    Clipbird.clipbird_bluetooth_manager_remove_bonded_devices_changed_callback(manager).also { callbacks.remove(manager.address()) }
  }

  fun bondedDevices(manager: MemorySegment): BluetoothDeviceList {
    return BluetoothDeviceList(Clipbird.clipbird_bluetooth_manager_bonded_devices(manager))
  }

  fun localName(manager: MemorySegment): String {
    return Clipbird.clipbird_bluetooth_manager_local_name(manager).orThrow {
      IOException("Failed to get Bluetooth local name: ${NativeErrorHandle.lastErrorMessage()}")
    }.reinterpret(Long.MAX_VALUE).getString(0)
  }

  @OptIn(ExperimentalUuidApi::class)
  fun connect(manager: MemorySegment, address: String, serviceUuid: String): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_bluetooth_manager_connect_rfcomm(manager, arena.allocateFrom(address), arena.allocateFrom(serviceUuid))
    }.orThrow {
      IOException("Failed to connect to Bluetooth device: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  @OptIn(ExperimentalUuidApi::class)
  fun start(manager: MemorySegment, serviceName: String, serviceUuid: String): MemorySegment {
    return Arena.ofConfined().use { arena ->
      Clipbird.clipbird_bluetooth_manager_start_rfcomm_server(manager, arena.allocateFrom(serviceName), arena.allocateFrom(serviceUuid))
    }.orThrow {
      IOException("Failed to start Bluetooth server: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun create(): MemorySegment {
    return Clipbird.clipbird_bluetooth_manager_create().orThrow {
      IOException("Failed to create native Bluetooth manager: ${NativeErrorHandle.lastErrorMessage()}")
    }
  }

  fun destroy(manager: MemorySegment) {
    Clipbird.clipbird_bluetooth_manager_destroy(manager)
  }

  @Suppress("unused")
  @JvmStatic
  fun dispatchCallback(context: MemorySegment) {
    callbacks[context.address()]?.invoke()
  }
}

class BluetoothDeviceList(
  private val handle: MemorySegment
) : AutoCloseable {
  fun address(index: Long): String {
    return Clipbird.clipbird_bluetooth_device_address(handle, index).reinterpret(Long.MAX_VALUE).getString(0)
  }

  fun name(index: Long): String {
    return Clipbird.clipbird_bluetooth_device_name(handle, index).reinterpret(Long.MAX_VALUE).getString(0)
  }

  val size: Long get() = Clipbird.clipbird_bluetooth_device_list_size(handle)

  override fun close() {
    Clipbird.clipbird_bluetooth_device_list_destroy(handle)
  }
}
