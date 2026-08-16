package com.devtech.accahelps.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// Data model for our state
data class SectionState(
    val sectionId: String,
    val isEnabled: MutableState<Boolean> = mutableStateOf(true),
    val sourcesState: List<SourceState> = Source.entries.map {
        SourceState(it)
    }
)

data class SourceState(
    val source: Source,
    val isSelected: MutableState<Boolean> = mutableStateOf(true)
)

