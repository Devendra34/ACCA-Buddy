package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.domain.QuestionMapper
import com.devtech.accahelps.domain.QuestionRow
import com.devtech.accahelps.domain.store.QuestionsStore
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.toSourceOrNull
import com.devtech.accahelps.sheets.impl.SpreadsheetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(
    private val spreadsheetLoader: SpreadsheetLoader = SpreadsheetLoader(),
    private val questionsStore: QuestionsStore,
    private val sectionsStore: SectionsStore,
    private val mapper: QuestionMapper = QuestionMapper(),
) {

    suspend fun syncFromSheet() = withContext(Dispatchers.IO) {
        val (questionsUrl, rulesUrl) = parseCurrentQuestionsAndRulesUrls()

        clearAndInsertQuestions(questionsUrl)
        clearAndInsertSections(rulesUrl)
    }

    private suspend fun clearAndInsertQuestions(questionsUrl: String) {
        val rows = parseQuestionsCsv(questionsUrl)

        val allExpandedQuestions = rows.flatMap { mapper.mapRowToQuestions(it) }.filter {
            it.questionNumber.isNotBlank()
        }
        questionsStore.clearAndInsertQuestions(allExpandedQuestions)
    }

    private suspend fun clearAndInsertSections(rulesUrl: String) {
        val data = spreadsheetLoader.fetchTable(rulesUrl)
        val sections = data.drop(1) // Drop header
            .mapNotNull { row ->
                val sectionId = row.getOrNull(0) ?: return@mapNotNull null
                val totalQuestions = row.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
                val importantQuestions = row.getOrNull(2)?.toIntOrNull()
                Section(
                    id = sectionId,
                    totalQuestions = totalQuestions,
                    importantQuestions = importantQuestions
                )
            }

        sectionsStore.clearAndInsertSections(sections)
    }

    private suspend fun parseQuestionsCsv(csvUrl: String): List<QuestionRow> {
        val data = spreadsheetLoader.fetchTable(csvUrl)
        return data.drop(1) // Drop header
            .mapNotNull { row ->
                val source = row.getOrNull(0)?.toSourceOrNull() ?: return@mapNotNull null
                val sectionId = row.getOrNull(1) ?: return@mapNotNull null
                QuestionRow(
                    source = source,
                    sectionId = sectionId,
                    type = row.getOrNull(2) ?: "",
                    chapterRange = row.getOrNull(3) ?: "",
                    questionRange = row.getOrNull(4) ?: "",
                    importantRange = row.getOrNull(5) ?: ""
                )
            }
    }

    private suspend fun parseCurrentQuestionsAndRulesUrls(): Pair<String, String> {
        val data = spreadsheetLoader.fetchTable(MASTER_URL)
        return data.drop(1).getOrNull(0).let {
            val questionsUrl = it?.getOrNull(1).formatExportUrl()
            val rulesUrl = it?.getOrNull(2).formatExportUrl()
            questionsUrl to rulesUrl
        }
    }

    suspend fun hasData(): Boolean {
        return questionsStore.hasData()
    }

    private fun String?.formatExportUrl(): String {
        return this?.replace("/edit?", "/export?format=csv&").orEmpty()
    }

    companion object {
        private const val BASE_URL = "https://docs.google.com/spreadsheets/d"
        private const val SHEET_ID = "1pIwMEt1CvdqKfw9K04Hccw2H7DlfnH_pCX7_4G1M6QQ"

        private const val MASTER_URL =
            "${BASE_URL}/$SHEET_ID/export?format=csv&gid=1894786656#gid=1894786656"
    }
}
