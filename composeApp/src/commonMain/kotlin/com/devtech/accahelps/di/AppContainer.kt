package com.devtech.accahelps.di

import androidx.compose.runtime.Stable
import com.devtech.accahelps.data.repo.DemoQuestionsRepository
import com.devtech.accahelps.data.source.InMemoryDbStore
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.repo.SyncRepository
import com.devtech.accahelps.domain.store.AppDatabaseHelper
import com.devtech.accahelps.domain.store.QuestionsStore
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.domain.store.SettingsStore

@Stable
class AppContainer(
    val questionRepository: IQuestionRepository,
    val appDbHelper: AppDatabaseHelper,
    val syncRepository: SyncRepository = SyncRepository(questionsStore = appDbHelper.questionsStore, sectionsStore = appDbHelper.sectionsStore),
) {

    companion object {
        fun demo(): AppContainer {
            val inMemoryStore = InMemoryDbStore()
            return AppContainer(
                questionRepository = DemoQuestionsRepository(inMemoryStore),
                appDbHelper = object : AppDatabaseHelper {
                    override val settingStore: SettingsStore = inMemoryStore
                    override val questionsStore: QuestionsStore = inMemoryStore
                    override val sectionsStore: SectionsStore = inMemoryStore
                },
            )
        }
    }

}
