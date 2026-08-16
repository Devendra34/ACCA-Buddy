package com.devtech.accahelps.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.QuestionEntity
import com.devtech.accahelps.SectionSelectionEntity
import com.devtech.accahelps.data.source.QuestionsStoreDb
import com.devtech.accahelps.data.source.SectionsStoreDb
import com.devtech.accahelps.data.source.SettingsStoreDb
import com.devtech.accahelps.domain.store.AppDatabaseHelper
import com.devtech.accahelps.domain.store.QuestionsStore
import com.devtech.accahelps.domain.store.SectionsStore
import com.devtech.accahelps.domain.store.SettingsStore
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.model.toSourceOrNull

interface DriverFactory {
    fun createDriver(): SqlDriver
}

fun provideAppDb(driverFactory: DriverFactory): AppDatabaseHelper {
    val database = createDatabase(driverFactory)
    return provideAppDb(database)
}
fun provideAppDb(appDatabase: AppDatabase): AppDatabaseHelper {
    return object : AppDatabaseHelper {
        override val settingStore: SettingsStore = SettingsStoreDb(appDatabase)
        override val questionsStore: QuestionsStore= QuestionsStoreDb(appDatabase)
        override val sectionsStore: SectionsStore= SectionsStoreDb(appDatabase)
    }
}

expect fun provideDriverFactory(): DriverFactory

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    val sourceAdapter = SourceAdapter()
    return AppDatabase(
        driver, QuestionEntity.Adapter(
            sourceAdapter = sourceAdapter,
        ),
        SectionSelectionEntity.Adapter(
            selectedSourcesAdapter = SourcesAdapter(),
        )
    )
}

const val DB_NAME = "acca_buddy.db"


class SourceAdapter : ColumnAdapter<Source, String> {
    override fun decode(databaseValue: String): Source {
        return databaseValue.toSourceOrNull()!!
    }

    override fun encode(value: Source): String {
        return value.label
    }
}

class SourcesAdapter : ColumnAdapter<HashSet<Source>, String> {
    override fun decode(databaseValue: String): HashSet<Source> =
        if (databaseValue.isEmpty()) hashSetOf()
        else databaseValue.split(",").map { Source.valueOf(it) }.toHashSet()

    override fun encode(value: HashSet<Source>): String =
        value.joinToString(separator = ",") { it.name }
}
