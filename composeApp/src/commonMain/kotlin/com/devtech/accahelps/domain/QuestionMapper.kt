package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.util.RangeParser

data class QuestionRow(
    val source: Source,
    val sectionId: String,
    val type: String,
    val chapterRange: String,
    val questionRange: String,
    val importantRange: String
)

class QuestionMapper {

    fun mapRowToQuestions(row: QuestionRow): List<Question> {
        val source = row.source
        val sectionId = row.sectionId

        val chapters = if (row.chapterRange.isNotBlank())
            RangeParser.expand(row.chapterRange) else listOf(-1)
        val questions = RangeParser.expand(row.questionRange)
        if (questions.isEmpty()) {
            println("Error parsing: $row")
        }

        return chapters.flatMap { ch ->
            questions.map { q ->
                val isImp = RangeParser.isImportant(q, row.importantRange)

                Question(
                    source = source,
                    sectionId = sectionId,
                    questionNumber = q.toString(),
                    isImportant = isImp,
                    questionType = row.type,
                    chapterNumber = ch.takeIf { it != -1 }?.toString(),
                )
            }
        }
    }
}
