package com.finance.finstreak.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.data.model.DayEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class FilterPeriod(val label: String) {
    ALL("All"),
    WEEK("This Week"),
    MONTH("This Month"),
    THREE_MONTHS("3 Months")
}

data class HistoryUiState(
    val isLoading: Boolean = true,
    val entries: List<DayEntry> = emptyList(),
    val searchQuery: String = "",
    val filterPeriod: FilterPeriod = FilterPeriod.ALL,
    val error: String? = null
)

class HistoryViewModel(
    private val repository: DayEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _filterPeriod = MutableStateFlow(FilterPeriod.ALL)

    init {
        loadEntries()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun loadEntries() {
        viewModelScope.launch {
            combine(
                _searchQuery.debounce(300),
                _filterPeriod
            ) { query, period -> Pair(query, period) }
                .flatMapLatest { (query, period) ->
                    val now = LocalDate.now()
                    val from = when (period) {
                        FilterPeriod.ALL -> LocalDate.of(2000, 1, 1)
                        FilterPeriod.WEEK -> now.minusWeeks(1)
                        FilterPeriod.MONTH -> now.minusMonths(1)
                        FilterPeriod.THREE_MONTHS -> now.minusMonths(3)
                    }
                    if (query.isBlank()) {
                        repository.getEntriesInRange(from, now)
                    } else {
                        repository.searchEntries(query)
                    }
                }
                .collect { entries ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        entries = entries
                    )
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onFilterPeriodChanged(period: FilterPeriod) {
        _filterPeriod.value = period
        _uiState.value = _uiState.value.copy(filterPeriod = period)
    }

    fun reload() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadEntries()
    }
}
