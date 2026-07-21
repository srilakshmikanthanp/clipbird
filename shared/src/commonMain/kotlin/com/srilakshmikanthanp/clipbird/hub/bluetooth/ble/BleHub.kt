package com.srilakshmikanthanp.clipbird.hub.bluetooth.ble

import com.srilakshmikanthanp.clipbird.hub.Advertiser
import com.srilakshmikanthanp.clipbird.hub.Discoverer
import com.srilakshmikanthanp.clipbird.hub.Hub

class BleHub(
  private val advertiser: Advertiser,
  private val discoverer: Discoverer<BleHubDevice>
) : Hub<BleHubDevice>,
    Advertiser by advertiser,
    Discoverer<BleHubDevice> by discoverer
