package com.finance.finstreak.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.ui.components.HistoryListItem
import com.finance.finstreak.ui.components.SkeletonCard
import com.finance.finstreak.ui.navigation.NavRoutes
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddDay: () -> Unit,
    onViewHistory: () -> Unit,
    onViewAnalytics: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDay,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_add_day)
                )
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(dimens.spacingMd),
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)
                ) { repeat(4) { SkeletonCard() } }
            }

            state.error != null -> {
                ErrorStateView(
                    message = state.error!!,
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(dimens.spacingMd),
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingMd)
                ) {
                    item { StreakHeroCard(state = state) }
                    item { QuickStatsRow(state = state) }

                    item {
                        Button(
                            onClick = onAddDay,
                            modifier = Modifier.fillMaxWidth().height(dimens.buttonHeight),
                            shape = RoundedCornerShape(dimens.spacingMd),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.size(dimens.spacingSm))
                            Text(
                                text = stringResource(R.string.home_btn_log_today),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    if (state.recentEntries.isEmpty()) {
                        item { EmptyMotivationCard() }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = stringResource(R.string.home_recent_days), style = MaterialTheme.typography.titleLarge)
                                Text(
                                    text = stringResource(R.string.home_see_all),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = colors.primary,
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onViewHistory() }
                                )
                            }
                        }
                        items(state.recentEntries) { entry ->
                            HistoryListItem(
                                entry = entry,
                                onClick = { onNavigate(NavRoutes.dayDetail(entry.id)) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StreakHeroCard(state: HomeUiState) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val streak = state.streakData?.currentStreak ?: 0
    val motivationRes = when {
        streak == 0 -> R.string.motivation_0
        streak < 3 -> R.string.motivation_1_2
        streak < 7 -> R.string.motivation_3_6
        streak < 14 -> R.string.motivation_7_13
        streak < 30 -> R.string.motivation_14_29
        else -> R.string.motivation_30_plus
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusLg),
        colors = CardDefaults.cardColors(containerColor = colors.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.elevationCard)
    ) {
        Column(
            modifier = Modifier.padding(dimens.spacingLg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = colors.secondary,
                    modifier = Modifier.size(dimens.iconSizeLg)
                )
                Text(
                    text = "$streak",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                )
            }
            Text(
                text = stringResource(R.string.home_streak_label),
                style = MaterialTheme.typography.titleMedium.copy(color = colors.onPrimary.copy(alpha = 0.85f))
            )
            Spacer(modifier = Modifier.height(dimens.spacingSm))
            Text(
                text = stringResource(motivationRes),
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.onPrimary.copy(alpha = 0.75f))
            )
        }
    }
}

@Composable
private fun QuickStatsRow(state: HomeUiState) {
    val dimens = LocalAppTheme.dimens
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
        StatCard(label = stringResource(R.string.home_stat_best_streak), value = "${state.streakData?.longestStreak ?: 0}", modifier = Modifier.weight(1f))
        StatCard(label = stringResource(R.string.home_stat_total_safe), value = "${state.streakData?.totalSafeDays ?: 0}", modifier = Modifier.weight(1f))
        StatCard(label = stringResource(R.string.home_stat_total_days), value = "${state.streakData?.totalDays ?: 0}", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(dimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(dimens.spacingSm + 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = colors.primary)
            )
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyMotivationCard() {
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusLg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(dimens.spacingLg), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🌱", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(dimens.spacingSm))
            Text(text = stringResource(R.string.home_empty_title), style = MaterialTheme.typography.titleLarge)
            Text(
                text = stringResource(R.string.home_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorStateView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Column(
        modifier = modifier.padding(dimens.spacingLg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(dimens.spacingMd))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = colors.primary)) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.size(dimens.spacingSm))
            Text(stringResource(R.string.action_retry))
        }
    }
}
