package com.srilakshmikanthanp.clipbird.hub

interface Hub<T: HubDevice>: Advertiser, Discoverer<T>
