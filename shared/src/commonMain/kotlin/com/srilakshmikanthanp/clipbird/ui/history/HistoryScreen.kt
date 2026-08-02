package com.srilakshmikanthanp.clipbird.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.srilakshmikanthanp.clipbird.peer.TransferState
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
        contentScale = ContentScale.Fit,
        modifier = Modifier.height(60.dp),
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
private fun SendCard(state: TransferState, onSend: () -> Unit) {
  val isSending = state is TransferState.Progress

  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Send clipboard to your devices",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.weight(1f),
        )
        Spacer(
          Modifier.width(12.dp)
        )

        when (state) {
          is TransferState.Progress -> {
            Text(
              text = if (state.total > 0) "${(state.current.toFloat() / state.total * 100).toInt()}%" else "Sending…",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          is TransferState.Success -> {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
              )
              Text(
                text = "Sent",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
              )
              Spacer(
                Modifier.width(4.dp)
              )
              Button(
                onClick = onSend,
                shape = CircleShape
              ) {
                Text("Send")
              }
            }
          }
          is TransferState.Failure -> {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(
                Icons.Outlined.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp),
              )
              Text(
                text = "Failed",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
              )
              Spacer(
                Modifier.width(4.dp)
              )
              Button(
                onClick = onSend,
                shape = CircleShape
              ) {
                Text("Retry")
              }
            }
          }
        }
      }

      AnimatedVisibility(
        visible = isSending,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier.padding(top = 16.dp)
      ) {
        when (state) {
          is TransferState.Progress -> {
            LinearProgressIndicator(
              progress = { state.current.toFloat() / state.total },
              modifier = Modifier.fillMaxWidth()
            )
          }

          else -> {
            LinearProgressIndicator(
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
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
  val transferState by viewModel.transferState.collectAsStateWithLifecycle()
  val history by viewModel.history.collectAsStateWithLifecycle()

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    Column(modifier = Modifier.widthIn(max = 600.dp).fillMaxSize()) {
      Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        SendCard(state = transferState, onSend = viewModel::sendClipboard)
      }

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
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
}
