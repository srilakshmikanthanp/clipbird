package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.RetryingAdvertiser
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.uuid.ExperimentalUuidApi

@Module
class BleAdvertiserModule {
  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bleAdvertiser(hostDeviceProvider: HostDeviceProvider): BleAdvertiser = BleAdvertiser(
    BluetoothConstants.clipbirdServiceUuid,
    hostDeviceProvider
  )

  @Single
  fun advertiser(bleAdvertiser: BleAdvertiser): Advertiser = RetryingAdvertiser(bleAdvertiser)
}
