package com.devtech.accahelps

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devtech.accahelps.di.AppContainer
import com.devtech.accahelps.ui.AppContent
import com.devtech.accahelps.ui.AppTopBar
import com.devtech.accahelps.ui.theme.AppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun QuestionPickerApp(
    appContainer: AppContainer = AppContainer.demo()
) {
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            appContainer.questionRepository,
            appContainer.appDbHelper.settingStore,
            appContainer.syncRepository,
        )
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AppTheme {
        Scaffold(
            topBar = {
                AppTopBar(scrollBehavior = scrollBehavior, viewModel = viewModel)
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.generateQuestions() },
                    icon = { Icon(Icons.Default.PlayArrow, null) },
                    text = { Text("Generate") }
                )
            }
        ) { padding ->
            AppContent(
                modifier = Modifier
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                viewModel = viewModel,
            )

        }

        LaunchedEffect(Unit) {
            viewModel.ensureDataIsLoaded()
        }
    }
}

