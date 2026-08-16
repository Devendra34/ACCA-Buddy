package com.devtech.accahelps.domain.store

interface AppDatabaseHelper {

    val settingStore: SettingsStore

    val questionsStore: QuestionsStore

    val sectionsStore: SectionsStore
}