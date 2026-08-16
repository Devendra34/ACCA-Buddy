package com.devtech.accahelps.domain.store

import com.devtech.accahelps.model.Section
import kotlinx.coroutines.flow.Flow

interface SectionsStore {

    suspend fun clearAndInsertSections(sections: List<Section>)

    fun getAllSections(): Flow<List<Section>>

    suspend fun getSection(id: String): Section?

}