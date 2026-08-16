package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.data.source.FixedQuestionsSet
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

interface IQuestionRepository {
    fun getQuestionsFlow(): Flow<List<Question>>

    fun getSectionsFlow(): Flow<List<Section>>

    suspend fun addQuestions(questions: List<Question>)

    suspend fun deleteQuestion(question: Question)

    fun getQuestionsFlow(sectionId: String, source: Source): Flow<List<Question>> {
        return getQuestionsFlow().map { it.filter { q -> q.source == source && q.sectionId == sectionId } }
    }

    suspend fun generateRandom(sectionId: String, sources: List<Source>): List<Question>

    fun canEdit() = true

}

@Serializable
data class AppState(
    val questions: List<Question> = FixedQuestionsSet.getInitialAssetQuestions(),
    val settings: AppSettings = AppSettings()
)