package com.devtech.accahelps.domain.store

import com.devtech.accahelps.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsStore {

    fun settingsFlow(): Flow<AppSettings>

    suspend fun updateAppSettings(appSettings: AppSettings)
}