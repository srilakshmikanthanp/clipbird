package com.srilakshmikanthanp.clipbird.ui.license

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import clipbird.shared.generated.resources.Res
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries

@Composable
fun LicensesScreen() {
  val libraries by produceLibraries { Res.readBytes("files/aboutlibraries.json").decodeToString() }
  LibrariesContainer(libraries, modifier = Modifier.fillMaxSize())
}