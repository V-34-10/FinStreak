package com.finance.finstreak.ui.screen.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.datastore.UserPreferencesDataStore
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.util.cancelDailyReminder
import com.finance.finstreak.util.scheduleDailyReminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val showClearDataConfirm: Boolean = false,
    val showResetConfirm: Boolean = false,
    val isClearing: Boolean = false,
    val clearSuccess: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(
    private val repository: DayEntryRepository,
    private val prefs: UserPreferencesDataStore,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val notifEnabled = prefs.isNotificationEnabled.first()
            _uiState.value = _uiState.value.copy(notificationsEnabled = notifEnabled)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setNotificationEnabled(enabled)
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
            if (enabled) {
                scheduleDailyReminder(context)
            } else {
                cancelDailyReminder(context)
            }
        }
    }

    fun showClearDataConfirm() {
        _uiState.value = _uiState.value.copy(showClearDataConfirm = true)
    }

    fun dismissClearDataConfirm() {
        _uiState.value = _uiState.value.copy(showClearDataConfirm = false)
    }

    fun showResetConfirm() {
        _uiState.value = _uiState.value.copy(showResetConfirm = true)
    }

    fun dismissResetConfirm() {
        _uiState.value = _uiState.value.copy(showResetConfirm = false)
    }

    fun clearAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true, showClearDataConfirm = false)
            try {
                repository.deleteAllEntries()
                _uiState.value = _uiState.value.copy(isClearing = false, clearSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isClearing = false,
                    error = e.message ?: "Failed to clear data"
                )
            }
        }
    }

    fun resetAllSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true, showResetConfirm = false)
            try {
                repository.deleteAllEntries()
                prefs.clearAll()
                _uiState.value = _uiState.value.copy(isClearing = false, clearSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isClearing = false,
                    error = e.message ?: "Failed to reset"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessFlag() {
        _uiState.value = _uiState.value.copy(clearSuccess = false)
    }
}
