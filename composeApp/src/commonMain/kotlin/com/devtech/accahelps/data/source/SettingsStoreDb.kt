package com.devtech.accahelps.data.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.SectionSelectionEntity
import com.devtech.accahelps.domain.store.SettingsStore
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.SectionSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsStoreDb(private val database: AppDatabase) : SettingsStore {

    val queries get() = database.appSettingsQueries
    private val context = Dispatchers.IO

    override fun settingsFlow(): Flow<AppSettings> = queries.getSelections()
        .asFlow()
        .mapToList(context)
        .map { selectionEntities ->
            AppSettings(selectionEntities.map {
                SectionSelection(it.sectionId, it.isEnabled, it.selectedSources)
            })
        }

    override suspend fun updateAppSettings(appSettings: AppSettings) {
        withContext(context) {
            database.transaction {
                appSettings.sectionSelections.forEach {
                    queries.updateSectionSelection(
                        SectionSelectionEntity(
                            it.sectionId, it.isEnabled, it.selectedSources
                        )
                    )
                }
            }
        }
    }
}