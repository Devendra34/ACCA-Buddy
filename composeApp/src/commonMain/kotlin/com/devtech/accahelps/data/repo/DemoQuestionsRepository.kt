package com.devtech.accahelps.data.repo

import androidx.compose.runtime.Stable
import com.devtech.accahelps.data.source.FixedQuestionsSet
import com.devtech.accahelps.domain.QuestionsSelector
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
class DemoQuestionsRepository(
    private val sectionStore: SectionsStore,
) : IQuestionRepository {
    private val questions = MutableStateFlow(
        FixedQuestionsSet.getInitialAssetQuestions()
    )

    override fun getQuestionsFlow(): Flow<List<Question>> = questions.asStateFlow()

    override fun getSectionsFlow(): Flow<List<Section>> {
        return sectionStore.getAllSections()
    }

    override suspend fun generateRandom(sectionId: String, sources: List<Source>): List<Question> {
        return QuestionsSelector.selectedQuestions(
            sectionId,
            sources,
            questions.value,
            5
        )
    }

    override suspend fun addQuestions(questions: List<Question>) {
        updateQuestions { current -> current + questions }
    }

    override suspend fun deleteQuestion(question: Question) {
        updateQuestions { current -> current.filter { it != question } }
    }

    private fun updateQuestions(transform: (List<Question>) -> List<Question>) {
        questions.update { transform(it) }
    }
}