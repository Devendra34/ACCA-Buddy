package com.devtech.accahelps.ui.popup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.devtech.accahelps.domain.QuestionRangeInput
import com.devtech.accahelps.model.Source

@Composable
fun AddQuestionDialog(
    section: String,
    selectedSource: Source,
    onDismiss: () -> Unit,
    onConfirm: (QuestionRangeInput) -> Unit
) {
    var rangeInput by remember { mutableStateOf("") } // "1-20"
    var chInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf("") }
    val widthModifier = Modifier.fillMaxWidth()
    var isImportant by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AddQuestionsTitle(widthModifier, section, selectedSource)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Source Selector
//                SourceDropdown(selectedSource) { selectedSource = it }


                // Study Hub specific fields
                if (selectedSource == Source.StudyHub) {
                    OutlinedTextField(
                        modifier = widthModifier,
                        value = typeInput,
                        onValueChange = { typeInput = it },
                        label = { Text("Type (e.g. OT)") })
                    OutlinedTextField(
                        modifier = widthModifier,
                        value = chInput,
                        onValueChange = { chInput = it },
                        label = { Text("Chapter") })
                }

                OutlinedTextField(
                    modifier = widthModifier,
                    value = rangeInput,
                    onValueChange = { rangeInput = it },
                    label = { Text("Question Range (e.g. 1-20)") },
                    placeholder = { Text("1-5, 10, or just 5") }
                )
                Row(
                    modifier = widthModifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mark as Important ⭐", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isImportant,
                        onCheckedChange = { isImportant = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    QuestionRangeInput(
                        selectedSource,
                        section,
                        rangeInput,
                        typeInput,
                        chInput,
                        isImportant
                    )
                )
                onDismiss()
            }) { Text("Add") }
        }
    )
}

@Composable
private fun AddQuestionsTitle(
    modifier: Modifier = Modifier,
    sectionId: String,
    selectedSource: Source
) {

    val headlineStyle = MaterialTheme.typography.headlineSmall
    val bodyStyle = MaterialTheme.typography.bodyMedium
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(style = bodyStyle.toSpanStyle()) {
                append("Add Questions to\n")
            }
            withStyle(
                style = headlineStyle.toSpanStyle().copy(fontWeight = FontWeight.Bold)
            ) {
                append("$sectionId - ${selectedSource.label}")
            }
        },
        textAlign = TextAlign.Center
    )
}
