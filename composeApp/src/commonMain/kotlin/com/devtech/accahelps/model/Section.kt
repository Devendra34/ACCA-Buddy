package com.devtech.accahelps.model

import kotlinx.serialization.Serializable

@Serializable
data class Section(
    val id: String,
    val totalQuestions: Int,
    val importantQuestions: Int? = null,
)
