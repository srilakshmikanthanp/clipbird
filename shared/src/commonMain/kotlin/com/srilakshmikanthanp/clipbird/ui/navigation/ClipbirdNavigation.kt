package com.srilakshmikanthanp.clipbird.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import clipbird.shared.generated.resources.Res
import clipbird.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

enum class NavigationDrawerDestination(val label: String, val route: Any) {
  DEVICES("Devices", DevicesRoute),
}

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
    Text(text = "Clipbird", style = MaterialTheme.typography.headlineSmall)
  }
}

@Composable
private fun NavigationDrawerElements(
  selected: NavigationDrawerDestination,
  onDestinationClick: (NavigationDrawerDestination) -> Unit,
) {
  NavigationDrawerDestination.entries.forEach { destination ->
    NavigationDrawerItem(
      label = { Text(destination.label) },
      selected = destination == selected,
      onClick = { onDestinationClick(destination) },
      modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
  }
}

@Composable
private fun NavigationDrawerContent(
  selected: NavigationDrawerDestination,
  onDestinationClick: (NavigationDrawerDestination) -> Unit,
) {
  ModalDrawerSheet {
    NavigationDrawerHeader()

    HorizontalDivider(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    NavigationDrawerElements(
      selected = selected,
      onDestinationClick = onDestinationClick,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipbirdNavigation() {
  val snackBarHostState = remember { SnackbarHostState() }
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val coroutineScope = rememberCoroutineScope()
  val navController = rememberNavController()
  val currentBackStack by navController.currentBackStackEntryAsState()

  val selected = NavigationDrawerDestination.entries.firstOrNull { item ->
    currentBackStack?.destination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
  } ?: NavigationDrawerDestination.DEVICES

  fun navigateTo(destination: NavigationDrawerDestination) {
    navController.navigate(destination.route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      restoreState = true
      launchSingleTop = true
    }
    coroutineScope.launch {
      drawerState.close()
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      NavigationDrawerContent(
        selected = selected,
        onDestinationClick = { navigateTo(it) },
      )
    },
  ) {
    Scaffold(
      snackbarHost = {
        SnackbarHost(snackBarHostState)
      },
      topBar = {
        TopAppBar(
          title = { Text(selected.label) },
          navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
              Icon(Icons.Default.Menu, contentDescription = "Open navigation menu")
            }
          },
        )
      },
    ) { padding ->
      ClipbirdNavHost(
        navController = navController,
        snackbarHostState = snackBarHostState,
        modifier = Modifier.padding(padding),
      )
    }
  }
}
