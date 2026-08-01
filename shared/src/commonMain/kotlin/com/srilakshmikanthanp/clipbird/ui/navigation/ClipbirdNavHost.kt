package com.srilakshmikanthanp.clipbird.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.srilakshmikanthanp.clipbird.ui.about.AboutScreen
import com.srilakshmikanthanp.clipbird.ui.license.LicensesScreen
import com.srilakshmikanthanp.clipbird.ui.device.DevicesScreen
import com.srilakshmikanthanp.clipbird.ui.history.HistoryScreen
import com.srilakshmikanthanp.clipbird.ui.trust.TrustedScreen

@Composable
fun ClipbirdNavHost(
  navController: NavHostController,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
) {
  NavHost(
    navController = navController,
    startDestination = DevicesRoute,
    modifier = modifier,
  ) {
    composable<DevicesRoute> { DevicesScreen(snackbarHostState) }
    composable<HistoryRoute> { HistoryScreen() }
    composable<TrustedRoute> { TrustedScreen() }
    composable<AboutRoute> { AboutScreen(onNavigateToLicenses = { navController.navigate(LicensesRoute) }) }
    composable<LicensesRoute> { LicensesScreen() }
  }
}
