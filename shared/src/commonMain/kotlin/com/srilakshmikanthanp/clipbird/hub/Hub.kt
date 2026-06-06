package com.srilakshmikanthanp.clipbird.hub

interface Hub<T: HubDevice>: Advertiser<T>, Discoverer<T>
