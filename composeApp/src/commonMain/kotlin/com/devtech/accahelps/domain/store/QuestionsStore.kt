package com.devtech.accahelps.domain.store

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow

interface QuestionsStore {

    fun getQuestionsFlow(): Flow<List<Question>>

    fun getQuestionsFlow(sectionId: String, source: Source): Flow<List<Question>>

    suspend fun clearAndInsertQuestions(questions: List<Question>)

    suspend fun insertOrUpdateQuestions(questions: List<Question>)

    suspend fun deleteQuestion(question: Question)

    suspend fun getRandom(sectionId: String, sources: List<Source>, importantLimit: Int, totalLimit: Int): List<Question>

    suspend fun hasData(): Boolean
}