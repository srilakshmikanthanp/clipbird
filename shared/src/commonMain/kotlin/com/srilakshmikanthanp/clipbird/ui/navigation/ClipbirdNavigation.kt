package com.srilakshmikanthanp.clipbird.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import clipbird.shared.generated.resources.Res
import clipbird.shared.generated.resources.logo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
private fun NavigationDrawerHeader() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth().padding(24.dp),
  ) {
    Image(
      painter = painterResource(Res.drawable.logo),
      contentDescription = "Clipbird logo",
      modifier = Modifier.size(72.dp),
    )
    Spacer(Modifier.height(12.dp))
    Text(
      text = "Clipbird",
      style = MaterialTheme.typography.headlineSmall
    )
  }
}

@Composable
private fun NavigationDrawerElements(
  currentRoute: ClipbirdRoute,
  onRouteClick: (ClipbirdRoute) -> Unit,
) {
  routes.filterIsInstance<DrawerRoute>().forEach { route ->
    NavigationDrawerItem(
      label = { Text(route.label) },
      selected = route == currentRoute,
      onClick = { onRouteClick(route) },
      modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
  }
}

@Composable
private fun NavigationDrawerContent(
  currentRoute: ClipbirdRoute,
  onRouteClick: (ClipbirdRoute) -> Unit,
) {
  ModalDrawerSheet {
    NavigationDrawerHeader()
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    NavigationDrawerElements(currentRoute = currentRoute, onRouteClick = onRouteClick)
  }
}

@Composable
private fun NavigationButton(
  route: ClipbirdRoute,
  onClick: () -> Unit
) {
  IconButton(onClick = onClick) {
    when (route) {
      is BackRoute -> Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
      is DrawerRoute -> Icon(Icons.Default.Menu, contentDescription = "Open navigation menu")
    }
  }
}

@Composable
fun ClipbirdNavigation(initialRoute: DrawerRoute = DevicesRoute) {
  val snackBarHostState = remember { SnackbarHostState() }
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val coroutineScope = rememberCoroutineScope()
  val navController = rememberNavController()
  val currentBackStack by navController.currentBackStackEntryAsState()

  val currentRoute: ClipbirdRoute = routes.firstOrNull { route ->
    currentBackStack?.destination?.hierarchy?.any { it.hasRoute(route::class) } == true
  } ?: DevicesRoute

  fun navigateTo(route: ClipbirdRoute) {
    navController.navigate(route as Any) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      restoreState = true
      launchSingleTop = true
    }
    coroutineScope.launch {
      drawerState.close()
    }
  }

  LaunchedEffect(initialRoute) {
    if (initialRoute != DevicesRoute) {
      navigateTo(initialRoute)
    }
  }

  ModalNavigationDrawer(
    gesturesEnabled = currentRoute is DrawerRoute,
    drawerState = drawerState,
    drawerContent = {
      NavigationDrawerContent(
        currentRoute = currentRoute,
        onRouteClick = { navigateTo(it) },
      )
    },
  ) {
    Scaffold(
      snackbarHost = { SnackbarHost(snackBarHostState) },
      topBar = {
        TopAppBar(
          navigationIcon = {
            NavigationButton(
              route = currentRoute,
              onClick = {
                when (currentRoute) {
                  is BackRoute -> navController.popBackStack()
                  is DrawerRoute -> coroutineScope.launch { drawerState.open() }
                }
              },
            )
          },
          title = {
            Text(currentRoute.label)
          },
        )
      },
    ) { padding ->
      ClipbirdNavHost(
        navController = navController,
        snackbarHostState = snackBarHostState,
        startDestination = initialRoute,
        modifier = Modifier.padding(padding),
      )
    }
  }
}
