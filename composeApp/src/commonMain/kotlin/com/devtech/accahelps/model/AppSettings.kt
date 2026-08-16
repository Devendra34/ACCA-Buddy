package com.devtech.accahelps.model

import kotlinx.serialization.Serializable

@Serializable
data class SectionSelection(
    val sectionId: String,
    val isEnabled: Boolean,
    val selectedSources: HashSet<Source>
)

@Serializable
data class AppSettings(
    val sectionSelections: List<SectionSelection> = emptyList()
)