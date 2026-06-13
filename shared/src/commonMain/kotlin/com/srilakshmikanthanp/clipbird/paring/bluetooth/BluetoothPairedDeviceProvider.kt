package com.srilakshmikanthanp.clipbird.paring.bluetooth

import com.srilakshmikanthanp.clipbird.paring.PairedDeviceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BluetoothPairedDeviceProvider(
  private val bluetoothPairedDeviceService: BluetoothPairedDeviceService,
  private val scope: CoroutineScope,
): PairedDeviceProvider<BluetoothPairedDevice> {
  override val devices: StateFlow<Collection<BluetoothPairedDevice>> = bluetoothPairedDeviceService.getAll().stateIn(
    scope = scope,
    started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
    initialValue = emptySet(),
  )
}
