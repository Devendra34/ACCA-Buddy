package com.devtech.accahelps.data.source

import com.devtech.accahelps.domain.QuestionFactory
import com.devtech.accahelps.domain.QuestionRangeInput
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Source

object FixedQuestionsSet {

    fun getInitialAssetQuestions(): List<Question> {
        val allQuestions = mutableListOf<Question>()

        // --- KAPLAN ---
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Kaplan,
                    "A",
                    "1-229"
                )
            )
        )

        // Section B: 230-242 (Important), 243-262 (Normal)
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Kaplan,
                    "B",
                    "230-242",
                    isImportant = true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Kaplan,
                    "B",
                    "243-262"
                )
            )
        )

        // Section C: 263-291 (Normal), 292-314 (Important)
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Kaplan,
                    "C",
                    "263-291"
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Kaplan,
                    "C",
                    "292-314",
                    isImportant = true
                )
            )
        )

        // --- BPP ---
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Bpp,
                    "A",
                    "1-30, 32-61, 97-131, 171-200, 264-293",
                )
            )
        )

        // Section B: 62, 67, 72, 77, 82, 87, 92 (Important), others (Normal)
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Bpp,
                    "B",
                    "62, 67, 72, 77, 82, 87, 92",
                    isImportant = true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Bpp,
                    "B",
                    "31, 132, 137, 142, 147, 152, 157, 201, 206, 211, 216, 221, 226, 231, 236, 241, 246, 294, 299, 304, 309, 314, 319, 324, 329",
                )
            )
        )

        // Section C: 334-350 (Important), others (Normal)
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Bpp,
                    "C",
                    "162-170, 251-263",
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.Bpp,
                    "C",
                    "334-350",
                    isImportant = true
                )
            )
        )

        // --- STUDY HUB ---
        // 1. Quizzes (Normal)
        for (ch in 1..18) {
            allQuestions.addAll(
                QuestionFactory.newQuestions(
                    QuestionRangeInput(
                        Source.StudyHub,
                        "A",
                        "1-5",
                        "Quiz",
                        ch.toString(),
                        false
                    )
                )
            )
        }

        // 2. OT Revision (Normal)
        val otRevisionData = mapOf(
            1 to 11, 2 to 9, 3 to 33, 4 to 16, 5 to 15, 6 to 11,
            7 to 11, 8 to 7, 9 to 10, 10 to 18, 11 to 6, 12 to 12,
            13 to 14, 14 to 7, 15 to 8, 16 to 6, 17 to 14, 18 to 13
        )
        otRevisionData.forEach { (chapter, count) ->
            allQuestions.addAll(
                QuestionFactory.newQuestions(
                    QuestionRangeInput(
                        Source.StudyHub,
                        "A",
                        "1-$count",
                        "OT Revision",
                        chapter.toString(),
                        false
                    )
                )
            )
        }

        // 3. IMPORTANT CASES & QUESTIONS
        // Section C Study Hub
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "1-3",
                    "Revision Case",
                    "15",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "6-10",
                    "Revision Case",
                    "16",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "12",
                    "Revision Case",
                    "17",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "1, 7",
                    "Revision Case",
                    "18",
                    true
                )
            )
        )

        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "1",
                    "Study Question",
                    "15",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "1-3",
                    "Study Question",
                    "16",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "1, 2",
                    "Study Question",
                    "17",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "C",
                    "1, 2",
                    "Study Question",
                    "18",
                    true
                )
            )
        )

        // Section B Study Hub
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "B",
                    "1",
                    "Revision Case",
                    "2",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "B",
                    "4",
                    "Revision Case",
                    "3",
                    true
                )
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                QuestionRangeInput(
                    Source.StudyHub,
                    "B",
                    "4",
                    "Study Question",
                    "3",
                    true
                )
            )
        )

        return allQuestions
    }
}