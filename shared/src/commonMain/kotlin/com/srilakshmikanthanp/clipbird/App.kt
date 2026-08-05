package com.srilakshmikanthanp.clipbird

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srilakshmikanthanp.clipbird.ui.navigation.ClipbirdNavigation
import com.srilakshmikanthanp.clipbird.ui.navigation.DevicesRoute
import com.srilakshmikanthanp.clipbird.ui.navigation.DrawerRoute
import com.srilakshmikanthanp.clipbird.ui.theme.ClipbirdTheme

@Composable
fun App(initialRoute: DrawerRoute = DevicesRoute) {
  ClipbirdTheme {
    Surface(modifier = Modifier.fillMaxSize()) {
      ClipbirdNavigation(initialRoute = initialRoute)
    }
  }
}
