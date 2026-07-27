package com.srilakshmikanthanp.clipbird.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import clipbird.shared.generated.resources.Res
import clipbird.shared.generated.resources.logo
import com.srilakshmikanthanp.clipbird.AppConstants
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun ActionItem(
  icon: @Composable () -> Unit,
  text: String,
  modifier: Modifier,
) {
  Box(modifier = modifier) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth(),
    ) {
      icon()
      Text(
        text = text,
        modifier = Modifier.padding(5.dp),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
fun AboutScreen(viewModel: AboutViewModel = koinViewModel()) {
  val fingerprint by viewModel.fingerprint.collectAsStateWithLifecycle()
  var showFingerprintDialog by remember { mutableStateOf(false) }
  val uriHandler = LocalUriHandler.current

  if (showFingerprintDialog) {
    AlertDialog(
      onDismissRequest = { showFingerprintDialog = false },
      confirmButton = {
        TextButton(onClick = { showFingerprintDialog = false }) {
          Text("OK")
        }
      },
      title = { Text("Fingerprint") },
      text = {
        Text(
          text = fingerprint.ifEmpty { "Loading…" },
          style = MaterialTheme.typography.bodyMedium,
        )
      },
    )
  }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.padding(16.dp),
        ) {
          Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Clipbird logo",
            modifier = Modifier.size(140.dp),
          )
          Text(
            text = "Version ${AppConstants.APP_VERSION}",
            style = MaterialTheme.typography.bodyMedium,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "A cross-platform clipboard sharing application that syncs your clipboard seamlessly across devices.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }

      FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        val itemModifier = { onClick: () -> Unit ->
          Modifier
            .weight(1f)
            .padding(10.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
        }

        ActionItem(
          icon = { Icon(Icons.Outlined.Fingerprint, contentDescription = null, modifier = Modifier.size(32.dp)) },
          text = "Fingerprint",
          modifier = itemModifier { showFingerprintDialog = true },
        )
        ActionItem(
          icon = { Icon(Icons.Outlined.Language, contentDescription = null, modifier = Modifier.size(36.dp)) },
          text = "Website",
          modifier = itemModifier { uriHandler.openUri(AppConstants.APP_HOMEPAGE) },
        )
        ActionItem(
          icon = { Icon(Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(36.dp)) },
          text = "Source Code",
          modifier = itemModifier { uriHandler.openUri(AppConstants.APP_SOURCE_PAGE) },
        )
        ActionItem(
          icon = { Icon(Icons.Outlined.BugReport, contentDescription = null, modifier = Modifier.size(36.dp)) },
          text = "Report Issue",
          modifier = itemModifier { uriHandler.openUri(AppConstants.APP_ISSUES_PAGE) },
        )
        ActionItem(
          icon = { Icon(Icons.Outlined.Favorite, contentDescription = null, modifier = Modifier.size(36.dp)) },
          text = "Donate",
          modifier = itemModifier { uriHandler.openUri(AppConstants.APP_DONATE_PAGE) },
        )
      }
    }
  }
}