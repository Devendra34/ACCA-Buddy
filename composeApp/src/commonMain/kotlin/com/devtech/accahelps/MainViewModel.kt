package com.devtech.accahelps

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.devtech.accahelps.domain.QuestionFactory
import com.devtech.accahelps.domain.QuestionRangeInput
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.repo.SyncRepository
import com.devtech.accahelps.domain.store.SettingsStore
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.SectionSelection
import com.devtech.accahelps.model.SectionState
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


data class GeneratorUiState(
    val isPopupVisible: Boolean = false,
    val selectedQuestions: Map<String, List<Question>> = emptyMap(),
    val isLoading: Boolean = false
)

class MainViewModel(
    private val questionRepository: IQuestionRepository,
    private val settingsStore: SettingsStore,
    private val syncRepository: SyncRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    val canEditQuestions = questionRepository.canEdit()

    val viewQuestionsFor = MutableStateFlow<Pair<Source, String>?>(null)

    val addQuestionsFor = MutableStateFlow<Pair<Source, String>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewQuestions = viewQuestionsFor.flatMapLatest {
        if (it == null) return@flatMapLatest flowOf(emptyList())
        questionRepository.getQuestionsFlow(it.second, it.first)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val sectionsFlow = questionRepository.getSectionsFlow()
        .map { it.map { section -> SectionState(section.id) } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private suspend fun getSections() = sectionsFlow.first { it.isNotEmpty() }

    init {
        viewModelScope.launch {
            loadAndObserveSelections()
        }
    }

    private suspend fun loadAndObserveSelections() {
        val savedSettings = settingsStore.settingsFlow().first()
        val sections = getSections()

        savedSettings.sectionSelections.forEach { saved ->
            sections.find { it.sectionId == saved.sectionId }?.let { state ->
                state.isEnabled.value = saved.isEnabled
                state.sourcesState.forEach { sourceState ->
                    sourceState.isSelected.value =
                        saved.selectedSources.contains(sourceState.source)
                }
            }
        }

        snapshotFlow {
            sections.map {
                SectionSelection(
                    it.sectionId,
                    it.isEnabled.value,
                    it.sourcesState.filter { s -> s.isSelected.value }.map { s -> s.source }
                        .toHashSet()
                )
            }
        }.collect { currentSelections ->
            settingsStore.updateAppSettings(AppSettings(sectionSelections = currentSelections))
        }
    }

    fun generateQuestions() {
        val sections = sectionsFlow.value
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isLoading = true) }

            val results = sections.filter { it.isEnabled.value }.associate { selectedSection ->
                val selectedSources =
                    selectedSection.sourcesState.filter { sourceState -> sourceState.isSelected.value }
                        .map { it.source }
                val questions = questionRepository.generateRandom(
                    selectedSection.sectionId,
                    selectedSources
                )
                selectedSection.sectionId to questions
            }

            _uiState.update {
                it.copy(
                    selectedQuestions = results,
                    isPopupVisible = true,
                    isLoading = false
                )
            }
        }
    }

    fun dismissPopup() {
        _uiState.update { it.copy(isPopupVisible = false) }
    }

    fun addQuestionRange(
        input: QuestionRangeInput,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newOnes = QuestionFactory.newQuestions(input)
            questionRepository.addQuestions(newOnes)
        }
    }

    fun removeQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            questionRepository.deleteQuestion(question)
        }
    }

    fun onRequestSync() {
        viewModelScope.launch { requestSync() }
    }

    fun ensureDataIsLoaded() {
        viewModelScope.launch {
            val alreadyHasData = syncRepository.hasData()
            if (!alreadyHasData) {
                println("Initial launch detected: Fetching data from Sheet...")
                requestSync()
            } else {
                println("Data already present. Skipping initial sync.")
            }
        }
    }

    private suspend fun requestSync() {
        _isSyncing.value = true
        try {
            syncRepository.syncFromSheet()
        } catch (e: Exception) {
            println("Error $e")
        }
        _isSyncing.value = false
    }
}


class MainViewModelFactory(
    private val repository: IQuestionRepository,
    private val settingsStore: SettingsStore,
    private val syncRepository: SyncRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == MainViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, settingsStore, syncRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
