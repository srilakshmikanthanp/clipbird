package com.srilakshmikanthanp.clipbird.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardItem
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardMimeType
import org.koin.compose.viewmodel.koinViewModel

@Composable
private fun CompactItemContent(items: List<ClipboardItem>) {
  val image = items.firstOrNull { it.mimeType.startsWith("image/") }
  val text = items.firstOrNull { it.mimeType == ClipboardMimeType.MIME_TEXT }
    ?: items.firstOrNull { it.mimeType == ClipboardMimeType.MIME_HTML }

  when {
    image != null -> {
      val bitmap = remember(image.data) { decodeImageBitmap(image.data) }
      Image(
        bitmap = bitmap,
        contentDescription = "Image",
        contentScale = ContentScale.Fit,
        modifier = Modifier.height(40.dp),
      )
    }
    text != null -> {
      Text(
        text = text.data.decodeToString(),
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth(),
      )
    }
    else -> {
      Text(
        text = "[Unknown]",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun CompactSendCard(isSending: Boolean, onSend: () -> Unit) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Send clipboard to your devices",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.weight(1f),
      )
      Spacer(Modifier.width(8.dp))
      Button(onClick = onSend, enabled = !isSending, shape = CircleShape) {
        Text("Send", style = MaterialTheme.typography.labelSmall)
      }
    }
  }
}

@Composable
private fun CompactEmptyHistory() {
  Box(
    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "No clipboard history yet",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun CompactHistoryItem(
  content: ClipboardContent,
  onCopy: () -> Unit,
  onDelete: () -> Unit,
) {
  if (content.items.isEmpty()) return

  Card(modifier = Modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 2.dp),
    ) {
      CompactItemContent(content.items)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(Icons.Outlined.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

@Composable
fun CompactHistoryScreen(viewModel: HistoryViewModel = koinViewModel()) {
  val isSending by viewModel.isSending.collectAsStateWithLifecycle()
  val history by viewModel.history.collectAsStateWithLifecycle()

  Column(modifier = Modifier.fillMaxSize()) {
    Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
      CompactSendCard(isSending = isSending, onSend = viewModel::sendClipboard)
    }

    LazyColumn(
      modifier = Modifier.weight(1f).fillMaxWidth(),
      contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      if (history.isEmpty()) {
        item { CompactEmptyHistory() }
      } else {
        itemsIndexed(history) { index, content ->
          CompactHistoryItem(
            content = content,
            onCopy = { viewModel.copyToClipboard(index) },
            onDelete = { viewModel.deleteAt(index) },
          )
        }
      }
    }
  }
}
