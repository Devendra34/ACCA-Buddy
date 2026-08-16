package com.devtech.accahelps.data.source

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.SectionEntity
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.model.Section
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SectionsStoreDb(private val database: AppDatabase) : SectionsStore {

    val queries get() = database.sectionsQueries
    private val context = Dispatchers.IO

    override suspend fun clearAndInsertSections(sections: List<Section>) {
        withContext(context) {
            database.transaction {
                queries.clearAll()
                sections.distinctBy { it.id }.forEach {
                    queries.insertOrAddSection(SectionEntity(it.id, it.totalQuestions.toLong(), null))
                }
            }
        }
    }

    override fun getAllSections(): Flow<List<Section>> {
        return queries.getAllSections().mapToDomainFlow()
    }

    override suspend fun getSection(id: String): Section? {
        return queries.getSection(id).executeAsOneOrNull()?.let(::getDomainModel)
    }

    private fun Query<SectionEntity>.mapToDomainFlow(): Flow<List<Section>> {
        return this.asFlow().mapToList(Dispatchers.Default)
            .map { it.map(::getDomainModel) }
    }

    private fun getDomainModel(entity: SectionEntity): Section {
        return Section(id = entity.id, totalQuestions = entity.totalQuestions.toInt())
    }
}