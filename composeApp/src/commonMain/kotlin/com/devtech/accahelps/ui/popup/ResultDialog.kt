package com.devtech.accahelps.ui.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.devtech.accahelps.domain.QuestionsSelector
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.shareToWhatsApp
import com.devtech.accahelps.toClipEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResultDialog(
    questionsBySection: Map<String, List<Question>>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Allows full screen on mobile
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(0.9f) // 90% of screen size
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Question Set",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    questionsBySection.forEach { (sectionId, questions) ->
                        item {
                            Text(
                                text = sectionId,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        itemsIndexed(questions) { i, q ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.3f
                                    )
                                )
                            ) {
                                Text(
                                    text = "${i + 1}. ${q.fullPath}",
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                val clipboardManager = LocalClipboard.current
                val scope = rememberCoroutineScope()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ShareWaButton {
                        val formattedText = QuestionsSelector.formatQuestions(questionsBySection)
                        shareToWhatsApp(formattedText)
                    }
                    Spacer(Modifier.width(8.dp))
                    CopyButton {
                        val formattedText = QuestionsSelector.formatQuestions(questionsBySection)
                        scope.launch {
                            clipboardManager.setClipEntry(formattedText.toClipEntry())
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ShareWaButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(1.dp, Color(0xFF25D366)) // WhatsApp Green border
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Chat, // Or a custom WhatsApp SVG
            contentDescription = "Share to WhatsApp",
            tint = Color(0xFF25D366)
        )
        Spacer(Modifier.width(8.dp))
        Text("WhatsApp")
    }
}

@Composable
fun CopyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copy"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copy")
    }
}