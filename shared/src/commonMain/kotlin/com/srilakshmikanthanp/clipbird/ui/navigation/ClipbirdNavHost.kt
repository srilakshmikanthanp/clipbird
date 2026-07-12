package com.srilakshmikanthanp.clipbird.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.srilakshmikanthanp.clipbird.ui.pairing.DevicesScreen

@Composable
fun ClipbirdNavHost(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  NavHost(
    navController = navController,
    startDestination = DevicesRoute,
    modifier = modifier,
  ) {
    composable<DevicesRoute> {
      DevicesScreen()
    }
  }
}

