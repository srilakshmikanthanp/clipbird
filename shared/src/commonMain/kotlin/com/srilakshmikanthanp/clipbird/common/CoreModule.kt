package com.srilakshmikanthanp.clipbird.common

import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/** App-wide infrastructure and host identity. */
@Module
class CoreModule {
  @Single
  fun coroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  @Single
  fun hostDeviceProvider(dao: HostDeviceDao, bluetoothManager: BluetoothManager): HostDeviceProvider =
    HostDeviceProvider(dao, bluetoothManager.name)
}
