package com.devtech.accahelps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devtech.accahelps.model.SectionState
import com.devtech.accahelps.model.Source

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    section: SectionState,
    onViewSource: (Source) -> Unit,
    onEditSource: ((Source) -> Unit)?,
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        enabled = section.isEnabled.value,
        onClick = {},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = section.sectionId,
                    style = MaterialTheme.typography.titleLarge
                )
                Switch(
                    checked = section.isEnabled.value,
                    onCheckedChange = { section.isEnabled.value = it }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Source Options
            section.sourcesState.forEach { sourceState ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().height(48.dp) // Fixed height for alignment
                ) {
                    Checkbox(
                        checked = sourceState.isSelected.value,
                        onCheckedChange = { sourceState.isSelected.value = it },
                        enabled = section.isEnabled.value
                    )

                    Text(
                        text = sourceState.source.label,
                        modifier = Modifier.weight(1f), // Pushes the icon to the end
                        color = if (section.isEnabled.value) LocalContentColor.current else MaterialTheme.colorScheme.outline
                    )


                    if (section.isEnabled.value) {
                        IconButton(
                            onClick = { onViewSource(sourceState.source) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Visibility, // Or Icons.Default.Add
                                contentDescription = "View questions",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    onEditSource?.let {
                        if (section.isEnabled.value) {
                            IconButton(
                                onClick = { onEditSource(sourceState.source) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit, // Or Icons.Default.Add
                                    contentDescription = "Edit questions",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
