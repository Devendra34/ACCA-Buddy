package com.devtech.accahelps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devtech.accahelps.MainViewModel
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.ui.popup.AddQuestionDialog
import com.devtech.accahelps.ui.popup.ResultDialog
import com.devtech.accahelps.ui.popup.ViewQuestionsDialog

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {

    val sections by viewModel.sectionsFlow.collectAsStateWithLifecycle()
    val selectedSectionSource by viewModel.addQuestionsFor.collectAsStateWithLifecycle()
    val viewForSectionSource by viewModel.viewQuestionsFor.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 160.dp)
    ) {
        items(sections) { section ->
            SectionCard(
                section = section,
                onEditSource = { it: Source ->
                    viewModel.addQuestionsFor.value = it to section.sectionId
                }.takeIf { viewModel.canEditQuestions },
                onViewSource = { viewModel.viewQuestionsFor.value = it to section.sectionId },
            )
        }
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isPopupVisible) {
        ResultDialog(
            questionsBySection = uiState.selectedQuestions,
            onDismiss = { viewModel.dismissPopup() }
        )
    }
    selectedSectionSource?.let {
        AddQuestionDialog(
            selectedSource = it.first,
            section = it.second,
            onConfirm = { input ->
                viewModel.addQuestionRange(input)
                viewModel.addQuestionsFor.value = null
            },
            onDismiss = {
                viewModel.addQuestionsFor.value = null
            }
        )
    }
    viewForSectionSource?.let {
        ViewQuestionsDialog(
            source = it.first,
            section = it.second,
            viewModel = viewModel,
            onDismiss = { viewModel.viewQuestionsFor.value = null },
        )
    }
}