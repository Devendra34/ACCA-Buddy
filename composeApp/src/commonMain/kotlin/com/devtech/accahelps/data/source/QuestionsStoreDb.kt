package com.devtech.accahelps.data.source

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.QuestionEntity
import com.devtech.accahelps.data.model.toDomainModel
import com.devtech.accahelps.domain.store.QuestionsStore
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class QuestionsStoreDb(private val database: AppDatabase) : QuestionsStore {

    val queries get() = database.questionsQueries
    private val context = Dispatchers.IO

    override fun getQuestionsFlow(): Flow<List<Question>> {
        return queries.selectAll().mapToDomainFlow()
    }

    override fun getQuestionsFlow(sectionId: String, source: Source): Flow<List<Question>> {
        return queries.selectAllForSectionSouce(sectionId, source).mapToDomainFlow()
    }

    override suspend fun clearAndInsertQuestions(questions: List<Question>) {
        withContext(context) {
            database.transaction {
                queries.clearAll()
                questions.distinctBy { it.id }.forEach { q ->
                    queries.insertOrAddQuestion(q.toEntity())
                }
            }
        }
    }

    override suspend fun insertOrUpdateQuestions(questions: List<Question>) {
        withContext(context) {
            database.transaction {
                questions.distinctBy { it.id }.forEach { q ->
                    queries.insertOrAddQuestion(q.toEntity())
                }
            }
        }
    }

    override suspend fun deleteQuestion(question: Question) {
        withContext(context) {
            database.transaction {
                queries.deleteQuestion(question.toEntity().id)
            }
        }
    }

    override suspend fun getRandom(
        sectionId: String,
        sources: List<Source>,
        importantLimit: Int,
        totalLimit: Int
    ): List<Question> {
        val importantQuestions = queries
            .getRandomImportant(sectionId, sources, importantLimit.toLong())
            .executeAsList()

        val excludedIds = importantQuestions.map { it.id }
        val pendingLimit = totalLimit - importantQuestions.size
        val otherQuestions = queries.getRandom(
            sectionId, sources, excludedIds, pendingLimit.toLong()
        ).executeAsList()

        return (importantQuestions + otherQuestions).map { it.toDomainModel() }
    }


    override suspend fun hasData(): Boolean {
        return queries.hasData().executeAsOneOrNull() ?: false
    }

    private fun Query<QuestionEntity>.mapToDomainFlow(): Flow<List<Question>> {
        return this.asFlow().mapToList(Dispatchers.Default)
            .map { it.map { e -> e.toDomainModel() } }
    }


    private fun Question.toEntity(): QuestionEntity {
        return QuestionEntity(
            id = id,
            source = source,
            sectionId = sectionId,
            isImportant = isImportant,
            questionType = questionType,
            chapterNumber = chapterNumber,
            questionNumber = questionNumber,
        )
    }
}