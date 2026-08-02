package com.srilakshmikanthanp.clipbird.hub

import android.content.Context
import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import com.srilakshmikanthanp.clipbird.hub.bluetooth.BluetoothConstants
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleAdvertiser
import com.srilakshmikanthanp.clipbird.hub.bluetooth.ble.BleDiscoverer
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

@Module
class HubModule {
  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bleAdvertiser(context: Context, hostDeviceProvider: HostDeviceProvider): BleAdvertiser = BleAdvertiser(
    context,
    BluetoothConstants.clipbirdServiceUuid,
    hostDeviceProvider
  )

  @Single
  fun advertiser(bleAdvertiser: BleAdvertiser): Advertiser = RetryingAdvertiser(bleAdvertiser)

  @OptIn(ExperimentalUuidApi::class)
  @Single
  fun bleDiscoverer(): BleDiscoverer = BleDiscoverer(
    serviceUuid = BluetoothConstants.clipbirdServiceUuid,
    deviceTimeout = 30.seconds,
  )
}