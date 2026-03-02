package com.finance.finstreak.ui.screen.daydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.data.model.DayEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DayDetailUiState(
    val isLoading: Boolean = true,
    val entry: DayEntry? = null,
    val isDeleted: Boolean = false,
    val error: String? = null,
    val showDeleteConfirm: Boolean = false
)

class DayDetailViewModel(
    private val repository: DayEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DayDetailUiState())
    val uiState: StateFlow<DayDetailUiState> = _uiState.asStateFlow()

    fun loadEntry(dayId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val entry = repository.getEntryById(dayId)
                _uiState.value = _uiState.value.copy(isLoading = false, entry = entry)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load entry"
                )
            }
        }
    }

    fun showDeleteConfirm() {
        _uiState.value = _uiState.value.copy(showDeleteConfirm = true)
    }

    fun dismissDeleteConfirm() {
        _uiState.value = _uiState.value.copy(showDeleteConfirm = false)
    }

    fun deleteEntry() {
        val entry = _uiState.value.entry ?: return
        viewModelScope.launch {
            try {
                repository.deleteEntry(entry)
                _uiState.value = _uiState.value.copy(isDeleted = true, showDeleteConfirm = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete entry",
                    showDeleteConfirm = false
                )
            }
        }
    }
}
