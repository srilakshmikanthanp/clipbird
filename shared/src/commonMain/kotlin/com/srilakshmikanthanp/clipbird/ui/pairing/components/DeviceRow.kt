package com.srilakshmikanthanp.clipbird.ui.pairing.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A single device row: a name on the left and optional trailing content (spinner, action). */
@Composable
fun DeviceRow(
  name: String,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  trailing: @Composable (() -> Unit)? = null,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = name,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.bodyLarge,
    )
    if (trailing != null) trailing()
  }
}
