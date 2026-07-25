package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Discoverer
import kotlin.time.Duration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
expect class BleDiscoverer(serviceUuid: Uuid, deviceTimeout: Duration) : Discoverer<BleHubDevice>
