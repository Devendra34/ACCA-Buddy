package com.devtech.accahelps.data.source

import com.devtech.accahelps.domain.QuestionsSelector
import com.devtech.accahelps.domain.store.QuestionsStore
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.domain.store.SettingsStore
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map


class InMemoryDbStore : QuestionsStore, SettingsStore, SectionsStore {
    private val questions = MutableStateFlow<Set<Question>>(emptySet())
    private var appSettings = MutableStateFlow(AppSettings())

    private var sectionsFlow = MutableStateFlow(
        listOf(
            Section("A", 15),
            Section("B", 5),
            Section("C", 2),
        )
    )

    override fun getQuestionsFlow(): Flow<List<Question>> {
        return questions.map { it.toList() }
    }

    override fun getQuestionsFlow(sectionId: String, source: Source): Flow<List<Question>> {
        return getQuestionsFlow().map { it.filter { q -> q.source == source && q.sectionId == sectionId } }
    }

    override fun settingsFlow(): Flow<AppSettings> {
        return appSettings
    }

    override suspend fun clearAndInsertQuestions(questions: List<Question>) {
        val newQuestions = hashSetOf<Question>()
        newQuestions.addAll(questions)
        this.questions.value = newQuestions.toSet()
    }

    override suspend fun insertOrUpdateQuestions(questions: List<Question>) {
        val newQuestions = HashSet(this.questions.value)
        newQuestions.addAll(questions)
        this.questions.value = newQuestions.toSet()
    }

    override suspend fun deleteQuestion(question: Question) {
        val newQuestions = HashSet(this.questions.value)
        newQuestions.remove(question)
        this.questions.value = newQuestions.toSet()
    }

    override suspend fun updateAppSettings(appSettings: AppSettings) {
        this.appSettings.value = appSettings
    }

    override suspend fun getRandom(
        sectionId: String,
        sources: List<Source>,
        importantLimit: Int,
        totalLimit: Int
    ): List<Question> {
        return QuestionsSelector.selectedQuestions(
            sectionId,
            sources,
            questions.value.toList(),
            totalLimit
        )
    }

    override suspend fun hasData(): Boolean {
        return questions.value.isNotEmpty()
    }

    override suspend fun clearAndInsertSections(sections: List<Section>) {
        sectionsFlow.value = sections
    }

    override fun getAllSections(): Flow<List<Section>> {
        return sectionsFlow
    }

    override suspend fun getSection(id: String): Section? {
        return sectionsFlow.value.firstOrNull { it.id == id }
    }
}