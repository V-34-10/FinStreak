package com.finance.finstreak.ui.screen.addday

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

data class AddDayUiState(
    val selectedStatus: DayStatus? = null,
    val note: String = "",
    val noteError: String? = null,
    val statusError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val alreadyLoggedToday: Boolean = false,
    val error: String? = null
)

class AddDayViewModel(
    private val repository: DayEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDayUiState())
    val uiState: StateFlow<AddDayUiState> = _uiState.asStateFlow()

    private var existingTodayEntry: DayEntry? = null

    init {
        checkTodayEntry()
    }

    private fun checkTodayEntry() {
        viewModelScope.launch {
            val existing = repository.getEntryByDate(LocalDate.now())
            existingTodayEntry = existing
            if (existing != null) {
                _uiState.value = _uiState.value.copy(
                    selectedStatus = existing.status,
                    note = existing.note,
                    alreadyLoggedToday = true
                )
            }
        }
    }

    fun onStatusSelected(status: DayStatus) {
        _uiState.value = _uiState.value.copy(
            selectedStatus = status,
            statusError = null
        )
    }

    fun onNoteChanged(note: String) {
        val error = if (note.length > 200) "Note must be 200 characters or less" else null
        _uiState.value = _uiState.value.copy(note = note, noteError = error)
    }

    fun save() {
        val state = _uiState.value
        var hasError = false

        if (state.selectedStatus == null) {
            _uiState.value = _uiState.value.copy(statusError = "Please select a day status")
            hasError = true
        }
        if (state.note.length > 200) {
            _uiState.value = _uiState.value.copy(noteError = "Note must be 200 characters or less")
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val existing = existingTodayEntry
                if (existing != null) {
                    repository.updateEntry(
                        existing.copy(
                            status = state.selectedStatus!!,
                            note = state.note.trim()
                        )
                    )
                } else {
                    val newEntry = DayEntry(
                        date = LocalDate.now(),
                        status = state.selectedStatus!!,
                        note = state.note.trim()
                    )
                    val insertedId = repository.saveEntry(newEntry)
                    existingTodayEntry = newEntry.copy(id = insertedId)
                }
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true,
                    alreadyLoggedToday = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save entry"
                )
            }
        }
    }
}
