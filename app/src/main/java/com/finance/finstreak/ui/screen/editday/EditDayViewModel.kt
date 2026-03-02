package com.finance.finstreak.ui.screen.editday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.data.model.DayEntry
import com.finance.finstreak.data.model.DayStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditDayUiState(
    val isLoading: Boolean = true,
    val entry: DayEntry? = null,
    val selectedStatus: DayStatus? = null,
    val note: String = "",
    val noteError: String? = null,
    val statusError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val error: String? = null
)

class EditDayViewModel(
    private val repository: DayEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditDayUiState())
    val uiState: StateFlow<EditDayUiState> = _uiState.asStateFlow()

    fun loadEntry(dayId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val entry = repository.getEntryById(dayId)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                entry = entry,
                selectedStatus = entry?.status,
                note = entry?.note ?: ""
            )
        }
    }

    fun onStatusChanged(status: DayStatus) {
        _uiState.value = _uiState.value.copy(selectedStatus = status, statusError = null)
    }

    fun onNoteChanged(note: String) {
        val error = if (note.length > 200) "Max 200 characters" else null
        _uiState.value = _uiState.value.copy(note = note, noteError = error)
    }

    fun saveEdit() {
        val state = _uiState.value
        val entry = state.entry ?: return
        var hasError = false

        if (state.selectedStatus == null) {
            _uiState.value = _uiState.value.copy(statusError = "Please select a status")
            hasError = true
        }
        if (state.note.length > 200) {
            _uiState.value = _uiState.value.copy(noteError = "Max 200 characters")
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                repository.updateEntry(
                    entry.copy(
                        status = state.selectedStatus!!,
                        note = state.note.trim()
                    )
                )
                _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save"
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
                    error = e.message ?: "Failed to delete",
                    showDeleteConfirm = false
                )
            }
        }
    }
}
