package com.srilakshmikanthanp.clipbird.ui.navigation

import kotlinx.serialization.Serializable

sealed class ClipbirdRoute {
  abstract val label: String
}

sealed class DrawerRoute : ClipbirdRoute()

sealed class BackRoute : ClipbirdRoute()

@Serializable
data object DevicesRoute : DrawerRoute() {
  override val label = "Devices"
}

@Serializable
data object HistoryRoute : DrawerRoute() {
  override val label = "History"
}

@Serializable
data object TrustedRoute : DrawerRoute() {
  override val label = "Trust"
}

@Serializable
data object AboutRoute : DrawerRoute() {
  override val label = "About"
}

@Serializable
data object LicensesRoute : BackRoute() {
  override val label = "Open Source Licenses"
}

val routes: List<ClipbirdRoute> = listOf(
  DevicesRoute,
  HistoryRoute,
  TrustedRoute,
  AboutRoute,
  LicensesRoute
)
