package com.finance.finstreak.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.data.model.AnalyticsSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AnalyticsPeriod(val label: String, val days: Int) {
    WEEK("Week", 7),
    MONTH("Month", 30),
    THREE_MONTHS("3 Months", 90)
}

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val summary: AnalyticsSummary? = null,
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.MONTH,
    val error: String? = null
)

class AnalyticsViewModel(
    private val repository: DayEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun onPeriodChanged(period: AnalyticsPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period, isLoading = true)
        loadAnalytics(period.days)
    }

    fun loadAnalytics(periodDays: Int = AnalyticsPeriod.MONTH.days) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val summary = repository.getAnalyticsSummary(periodDays)
                _uiState.value = _uiState.value.copy(isLoading = false, summary = summary)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load analytics"
                )
            }
        }
    }
}
