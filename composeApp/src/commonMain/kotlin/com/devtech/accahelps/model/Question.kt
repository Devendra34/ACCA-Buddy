package com.devtech.accahelps.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val source: Source,
    val sectionId: String,
    val questionNumber: String,
    val isImportant: Boolean = false,

    // StudyHub-specific properties
    val questionType: String? = null,
    val chapterNumber: String? = null,
) {
    val id: String
        get() = toString()

    val fullPath: String
        get() {
            val items = listOf(source.label, sourcePath)
            return items.joinToString(PATH_SEPARATOR)
        }

    val sourcePath: String
        get() {
            val items = mutableListOf<String>()
            questionType?.takeIf { it.isNotBlank() }?.let { items.add(it) }
            chapterNumber?.takeIf { it.isNotBlank() }?.let { items.add("CH-$it") }
            items.add("Q$questionNumber${importantSuffix()}")
            return items.joinToString(PATH_SEPARATOR)
        }

    private fun importantSuffix(): String =
        if (isImportant) " ⭐" else ""

    companion object {
        private const val PATH_SEPARATOR = ": "
    }
}


fun List<Question>.questionFor(
    sectionId: String,
    source: Source
): List<Question> = filter { q -> q.sectionId == sectionId && q.source == source }