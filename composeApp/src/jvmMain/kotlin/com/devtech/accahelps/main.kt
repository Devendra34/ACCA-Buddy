package com.devtech.accahelps

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.devtech.accahelps.data.repo.DbQuestionsRepository
import com.devtech.accahelps.db.createDatabase
import com.devtech.accahelps.db.provideAppDb
import com.devtech.accahelps.db.provideDriverFactory
import com.devtech.accahelps.di.AppContainer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() {
    val appDbHelper = provideAppDb(createDatabase(provideDriverFactory()))
    val container = AppContainer(
        appDbHelper = appDbHelper,
        questionRepository = DbQuestionsRepository(
            appDbHelper.questionsStore,
            appDbHelper.sectionsStore
        )
    )
    application {
        val appName = stringResource(Res.string.app_name)
        val state = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            state = state,
            title = appName,
            icon = painterResource(Res.drawable.app_logo)
        ) {
            QuestionPickerApp(appContainer = container)
        }
    }
}