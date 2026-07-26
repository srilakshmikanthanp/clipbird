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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardContent
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardItem
import com.srilakshmikanthanp.clipbird.clipboard.ClipboardMimeType
import org.koin.compose.viewmodel.koinViewModel

internal expect fun decodeImageBitmap(data: ByteArray): ImageBitmap

@Composable
private fun HistoryItemContent(items: List<ClipboardItem>) {
  val image = items.firstOrNull {
    it.mimeType.startsWith("image/")
  }

  val text = items.firstOrNull {
    it.mimeType == ClipboardMimeType.MIME_TEXT
  } ?: items.firstOrNull {
    it.mimeType == ClipboardMimeType.MIME_HTML
  }

  when {
    image != null -> {
      val bitmap = remember(image.data) { decodeImageBitmap(image.data) }
      Image(
        bitmap = bitmap,
        contentDescription = "Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().height(60.dp),
      )
    }
    text != null -> {
      Text(
        text = text.data.decodeToString(),
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth(),
      )
    }
    else -> {
      Text(
        text = "[Unknown]",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun SendCard(onSend: () -> Unit) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Send clipboard to your devices",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.weight(1f),
      )
      Spacer(Modifier.width(12.dp))
      Button(
        onClick = onSend,
        shape = CircleShape
      ) {
        Text("Send")
      }
    }
  }
}

@Composable
private fun EmptyHistory() {
  Box(
    modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "No clipboard history yet",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun HistoryItem(
  content: ClipboardContent,
  onCopy: () -> Unit,
  onDelete: () -> Unit,
) {
  if (content.items.isEmpty()) return

  Card(modifier = Modifier.fillMaxWidth()) {
    Column(
      modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 4.dp),
    ) {
      HistoryItemContent(content.items)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        IconButton(onClick = onCopy) {
          Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy")
        }
        IconButton(onClick = onDelete) {
          Icon(Icons.Outlined.Delete, contentDescription = "Delete")
        }
      }
    }
  }
}

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = koinViewModel()) {
  val history by viewModel.history.collectAsStateWithLifecycle()

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SendCard(onSend = viewModel::sendClipboard)
      }

      if (history.isEmpty()) {
        item { EmptyHistory() }
      } else {
        itemsIndexed(history) { index, content ->
          HistoryItem(
            content = content,
            onCopy = { viewModel.copyToClipboard(index) },
            onDelete = { viewModel.deleteAt(index) },
          )
        }
      }
    }
  }
}
