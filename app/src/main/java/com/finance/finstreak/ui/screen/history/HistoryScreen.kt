package com.finance.finstreak.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.ui.components.EmptyStateView
import com.finance.finstreak.ui.components.ErrorView
import com.finance.finstreak.ui.components.HistoryListItem
import com.finance.finstreak.ui.components.SkeletonCard
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onDaySelected: (Long) -> Unit,
    onNavigate: (String) -> Unit
) {
    val viewModel: HistoryViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier.padding(horizontal = dimens.spacingMd, vertical = dimens.spacingSm),
                verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.history_search_placeholder)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.cd_search))
                    },
                    shape = RoundedCornerShape(dimens.radiusMd),
                    singleLine = true
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                    items(FilterPeriod.values()) { period ->
                        val label = when (period) {
                            FilterPeriod.ALL -> stringResource(R.string.filter_all)
                            FilterPeriod.WEEK -> stringResource(R.string.filter_week)
                            FilterPeriod.MONTH -> stringResource(R.string.filter_month)
                            FilterPeriod.THREE_MONTHS -> stringResource(R.string.filter_3months)
                        }
                        FilterChip(
                            selected = state.filterPeriod == period,
                            onClick = { viewModel.onFilterPeriodChanged(period) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary.copy(alpha = 0.15f),
                                selectedLabelColor = colors.primary
                            )
                        )
                    }
                }
            }

            when {
                state.isLoading -> {
                    Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                        repeat(5) { SkeletonCard() }
                    }
                }

                state.error != null -> {
                    ErrorView(message = state.error!!, onRetry = { viewModel.reload() }, modifier = Modifier.fillMaxSize())
                }

                state.entries.isEmpty() -> {
                    EmptyStateView(
                        emoji = "📋",
                        title = stringResource(R.string.history_empty_title),
                        subtitle = stringResource(R.string.history_empty_subtitle),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = dimens.spacingMd, vertical = dimens.spacingSm),
                        verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)
                    ) {
                        items(state.entries, key = { it.id }) { entry ->
                            HistoryListItem(entry = entry, onClick = { onDaySelected(entry.id) })
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}
