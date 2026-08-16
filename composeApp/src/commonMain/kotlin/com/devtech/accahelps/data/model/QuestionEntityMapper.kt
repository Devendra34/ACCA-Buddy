package com.devtech.accahelps.data.model

import com.devtech.accahelps.QuestionEntity
import com.devtech.accahelps.model.Question

fun QuestionEntity.toDomainModel(): Question {
    val isImp = isImportant == true
    return Question(
        source = source,
        sectionId = sectionId,
        questionNumber = questionNumber,
        isImportant = isImp,
        questionType = questionType.orEmpty(),
        chapterNumber = chapterNumber.orEmpty(),
    )
}