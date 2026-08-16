package com.devtech.accahelps.data.repo

import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.store.QuestionsStore
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow

class DbQuestionsRepository(
    private val questionsStore: QuestionsStore,
    private val sectionStore: SectionsStore,
) : IQuestionRepository {

    override fun getQuestionsFlow(): Flow<List<Question>> {
        return questionsStore.getQuestionsFlow()
    }

    override fun getSectionsFlow(): Flow<List<Section>> {
        return sectionStore.getAllSections()
    }

    override fun getQuestionsFlow(sectionId: String, source: Source): Flow<List<Question>> {
        return questionsStore.getQuestionsFlow(sectionId, source)
    }

    override suspend fun generateRandom(sectionId: String, sources: List<Source>): List<Question> {
        val section = sectionStore.getSection(sectionId)
        val importantLimit = section?.importantQuestions ?: 0
        val totalLimit = section?.totalQuestions ?: 0
        return questionsStore.getRandom(sectionId, sources, importantLimit, totalLimit)
    }

    override suspend fun addQuestions(questions: List<Question>) {
        questionsStore.insertOrUpdateQuestions(questions)
    }

    override suspend fun deleteQuestion(question: Question) {
        questionsStore.deleteQuestion(question)
    }

    override fun canEdit(): Boolean {
        return false
    }
}