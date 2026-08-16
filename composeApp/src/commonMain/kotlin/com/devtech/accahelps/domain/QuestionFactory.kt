package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Source

data class QuestionRangeInput(
    val source: Source,
    val sectionId: String,
    val inputRange: String,
    val type: String = "",
    val chapter: String = "",
    val isImportant: Boolean = false
)

object QuestionFactory {

    fun newQuestions(
        input: QuestionRangeInput,
    ): List<Question> {
        val questionsToAdd = mutableListOf<Question>()

        // 1. Split by comma to get discrete parts (e.g., "1-5, 10, 12-14")
        val segments = input.inputRange.split(",")

        for (segment in segments) {
            val trimmedSegment = segment.trim()
            if (trimmedSegment.isEmpty()) continue

            // 2. Check if the segment is a range (contains -, –, —, or " to ")
            val rangeParts = trimmedSegment.split(Regex("[-–—]| to "))
                .map { it.trim().filter { char -> char.isDigit() } }
                .filter { it.isNotEmpty() }

            if (rangeParts.size == 2) {
                // It's a range: e.g., "1-5"
                val start = rangeParts[0].toIntOrNull() ?: 0
                val end = rangeParts[1].toIntOrNull() ?: 0

                // Handle both ascending (1-5) and descending (5-1) if needed
                val range = if (start <= end) start..end else start downTo end
                for (i in range) {
                    questionsToAdd.add(
                        newQuestion(input, i.toString())
                    )
                }
            } else if (rangeParts.size == 1) {
                // It's a single number: e.g., "10"
                if (rangeParts[0].isBlank()) continue
                questionsToAdd.add(
                    newQuestion(input, rangeParts[0])
                )
            }
        }
        return questionsToAdd.distinctBy { it.id }
    }

    fun newQuestion(
        input: QuestionRangeInput,
        questionNumber: String,
    ): Question {
        return Question(
            source = input.source,
            sectionId = input.sectionId,
            questionNumber = questionNumber,
            isImportant = input.isImportant,
            questionType = input.type,
            chapterNumber = input.chapter,
        )
    }
}