package com.finance.finstreak.ui.screen.resilience

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.data.model.DayEntry
import com.finance.finstreak.data.model.DayStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ResilienceUiState(
    val scoreText: String = "",
    val scoreError: String? = null,
    val criteriaNote: String = "",
    val criteriaError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class ResilienceCalculatorViewModel(
    private val repository: DayEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResilienceUiState())
    val uiState: StateFlow<ResilienceUiState> = _uiState.asStateFlow()

    fun onScoreChanged(value: String) {
        val filtered = value.filter { it.isDigit() }
        val error = when {
            filtered.isEmpty() -> null
            filtered.toIntOrNull() == null -> "Must be an integer"
            filtered.toInt() !in 0..100 -> "Score must be between 0 and 100"
            else -> null
        }
        _uiState.value = _uiState.value.copy(scoreText = filtered, scoreError = error)
    }

    fun onCriteriaNoteChanged(value: String) {
        val error = if (value.length > 200) "Max 200 characters" else null
        _uiState.value = _uiState.value.copy(criteriaNote = value, criteriaError = error)
    }

    fun save() {
        val state = _uiState.value
        var hasError = false

        val score = state.scoreText.toIntOrNull()
        if (score == null || score !in 0..100) {
            _uiState.value = _uiState.value.copy(scoreError = "Enter a valid score (0–100)")
            hasError = true
        }
        if (state.criteriaNote.length > 200) {
            _uiState.value = _uiState.value.copy(criteriaError = "Max 200 characters")
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val today = LocalDate.now()
                val existing = repository.getEntryByDate(today)
                if (existing != null) {
                    repository.updateEntry(existing.copy(resilienceScore = score, criteriaNote = state.criteriaNote.trim()))
                } else {
                    repository.saveEntry(
                        DayEntry(
                            date = today,
                            status = DayStatus.SAFE,
                            resilienceScore = score,
                            criteriaNote = state.criteriaNote.trim()
                        )
                    )
                }
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save"
                )
            }
        }
    }
}
