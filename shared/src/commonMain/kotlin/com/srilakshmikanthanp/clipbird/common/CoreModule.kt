package com.srilakshmikanthanp.clipbird.common

import com.srilakshmikanthanp.clipbird.peer.authentication.Authenticator
import com.srilakshmikanthanp.clipbird.io.bluetooth.BluetoothManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class CoreModule {
  @Single
  fun hostDeviceProvider(dao: HostDeviceDao, bluetoothManager: BluetoothManager): HostDeviceProvider = HostDeviceProvider(
    dao, bluetoothManager.name
  )

  @Single
  fun authenticator(hostDeviceProvider: HostDeviceProvider): Authenticator = Authenticator(hostDeviceProvider)

  @Single
  fun coroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
