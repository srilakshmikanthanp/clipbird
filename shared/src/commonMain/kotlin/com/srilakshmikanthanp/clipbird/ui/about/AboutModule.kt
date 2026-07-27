package com.srilakshmikanthanp.clipbird.ui.about

import com.srilakshmikanthanp.clipbird.common.HostDeviceProvider
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module

@Module
class AboutModule {
  @KoinViewModel
  fun aboutViewModel(hostDeviceProvider: HostDeviceProvider): AboutViewModel =
    AboutViewModel(hostDeviceProvider)
}