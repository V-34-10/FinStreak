package com.finance.finstreak.ui.screen.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.data.model.AnalyticsSummary
import com.finance.finstreak.data.model.MonthlyPoint
import com.finance.finstreak.data.model.WeeklyPoint
import com.finance.finstreak.ui.components.EmptyStateView
import com.finance.finstreak.ui.components.ErrorView
import com.finance.finstreak.ui.components.SkeletonCard
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(onNavigate: (String) -> Unit) {
    val viewModel: AnalyticsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.analytics_title), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Column(modifier = Modifier.padding(paddingValues).padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                    repeat(5) { SkeletonCard() }
                }
            }

            state.error != null -> {
                ErrorView(message = state.error!!, onRetry = { viewModel.loadAnalytics() }, modifier = Modifier.fillMaxSize().padding(paddingValues))
            }

            state.summary == null || state.summary!!.totalSafeDays + state.summary!!.totalOverspendDays == 0 -> {
                EmptyStateView(
                    emoji = "📊",
                    title = stringResource(R.string.analytics_empty_title),
                    subtitle = stringResource(R.string.analytics_empty_subtitle),
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }

            else -> {
                val summary = state.summary!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(dimens.spacingMd),
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingMd)
                ) {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                            items(AnalyticsPeriod.values()) { period ->
                                val label = when (period) {
                                    AnalyticsPeriod.WEEK -> stringResource(R.string.analytics_period_week)
                                    AnalyticsPeriod.MONTH -> stringResource(R.string.analytics_period_month)
                                    AnalyticsPeriod.THREE_MONTHS -> stringResource(R.string.analytics_period_3months)
                                }
                                FilterChip(
                                    selected = state.selectedPeriod == period,
                                    onClick = { viewModel.onPeriodChanged(period) },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = colors.primary.copy(alpha = 0.15f),
                                        selectedLabelColor = colors.primary
                                    )
                                )
                            }
                        }
                    }
                    item { StreakSummarySection(summary) }
                    item { SafeRateCard(summary) }
                    item { WeeklyBarChart(summary.weeklyData) }
                    item { MonthlyBarChart(summary.monthlyData) }
                    item { TipsCard(summary) }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StreakSummarySection(summary: AnalyticsSummary) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
        AnalyticsStatCard(
            label = stringResource(R.string.analytics_current_streak),
            value = "${summary.currentStreak}",
            suffix = stringResource(R.string.analytics_days_suffix),
            color = colors.primary,
            modifier = Modifier.weight(1f)
        )
        AnalyticsStatCard(
            label = stringResource(R.string.analytics_best_streak),
            value = "${summary.longestStreak}",
            suffix = stringResource(R.string.analytics_days_suffix),
            color = colors.primary,
            modifier = Modifier.weight(1f)
        )
        AnalyticsStatCard(
            label = stringResource(R.string.analytics_avg_resilience),
            value = "${summary.averageResilienceScore.roundToInt()}",
            suffix = "/100",
            color = when {
                summary.averageResilienceScore >= 80 -> colors.primary
                summary.averageResilienceScore >= 50 -> colors.warning
                else -> colors.error
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AnalyticsStatCard(label: String, value: String, suffix: String, color: Color, modifier: Modifier = Modifier) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(dimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(dimens.spacingSm), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = color))
            Text(text = suffix, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(dimens.spacingXs))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun SafeRateCard(summary: AnalyticsSummary) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val total = summary.totalSafeDays + summary.totalOverspendDays
    val safePercent = if (total > 0) (summary.totalSafeDays.toFloat() / total * 100).roundToInt() else 0
    val barColor = when {
        safePercent >= 80 -> colors.primary
        safePercent >= 50 -> colors.warning
        else -> colors.error
    }
    val animatedProgress by animateFloatAsState(
        targetValue = if (total > 0) summary.totalSafeDays.toFloat() / total else 0f,
        animationSpec = tween(800),
        label = "safe_rate"
    )

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(dimens.radiusLg), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
        Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.analytics_safe_rate_title), style = MaterialTheme.typography.titleMedium)
                Text(text = "$safePercent%", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = barColor))
            }
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(colors.surfaceVariant)) {
                Box(modifier = Modifier.fillMaxWidth(animatedProgress).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(barColor))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${summary.totalSafeDays} ${stringResource(R.string.analytics_legend_safe).lowercase()}", style = MaterialTheme.typography.bodySmall, color = colors.primary)
                Text(text = "${summary.totalOverspendDays} ${stringResource(R.string.analytics_legend_overspend).lowercase()}", style = MaterialTheme.typography.bodySmall, color = colors.error)
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(weeklyData: List<WeeklyPoint>) {
    if (weeklyData.isEmpty()) return
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val maxVal = weeklyData.maxOf { it.totalDays.coerceAtLeast(1) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(dimens.radiusLg), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
        Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            Text(text = stringResource(R.string.analytics_weekly_title), style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm), verticalAlignment = Alignment.Bottom) {
                weeklyData.forEach { point ->
                    val safeFraction = if (point.totalDays > 0) point.safeDays.toFloat() / maxVal else 0f
                    val totalFraction = point.totalDays.toFloat() / maxVal
                    val animatedSafe by animateFloatAsState(targetValue = safeFraction, animationSpec = tween(600), label = "w_safe")
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.BottomCenter) {
                            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(totalFraction.coerceIn(0f, 1f)).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(colors.surfaceVariant))
                            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(animatedSafe.coerceIn(0f, 1f)).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(colors.primary))
                        }
                        Spacer(modifier = Modifier.height(dimens.spacingXs))
                        Text(text = point.label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)) {
                LegendItem(color = colors.primary, label = stringResource(R.string.analytics_legend_safe))
                LegendItem(color = colors.surfaceVariant, label = stringResource(R.string.analytics_legend_total))
            }
        }
    }
}

@Composable
private fun MonthlyBarChart(monthlyData: List<MonthlyPoint>) {
    if (monthlyData.isEmpty()) return
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val maxVal = monthlyData.maxOf { (it.safeDays + it.overspendDays).coerceAtLeast(1) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(dimens.radiusLg), colors = CardDefaults.cardColors(containerColor = colors.surface)) {
        Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            Text(text = stringResource(R.string.analytics_monthly_title), style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth().height(120.dp), horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm), verticalAlignment = Alignment.Bottom) {
                monthlyData.forEach { point ->
                    val safeFraction = point.safeDays.toFloat() / maxVal
                    val overspendFraction = point.overspendDays.toFloat() / maxVal
                    val animatedSafe by animateFloatAsState(targetValue = safeFraction.coerceIn(0f, 1f), animationSpec = tween(600), label = "m_safe")
                    val animatedOverspend by animateFloatAsState(targetValue = overspendFraction.coerceIn(0f, 1f), animationSpec = tween(600), label = "m_over")
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.BottomCenter) {
                            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                                if (animatedOverspend > 0f) Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(animatedOverspend).background(colors.error.copy(alpha = 0.7f)))
                                if (animatedSafe > 0f) Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(animatedSafe).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(colors.primary))
                            }
                        }
                        Spacer(modifier = Modifier.height(dimens.spacingXs))
                        Text(text = point.month, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)) {
                LegendItem(color = colors.primary, label = stringResource(R.string.analytics_legend_safe))
                LegendItem(color = colors.error.copy(alpha = 0.7f), label = stringResource(R.string.analytics_legend_overspend))
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    val dimens = LocalAppTheme.dimens
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(dimens.spacingXs)) {
        Box(modifier = Modifier.width(12.dp).height(12.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun TipsCard(summary: AnalyticsSummary) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val tips = buildTips(summary)

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(dimens.radiusLg), colors = CardDefaults.cardColors(containerColor = colors.warning.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            Text(text = stringResource(R.string.analytics_tips_title), style = MaterialTheme.typography.titleMedium)
            tips.forEach { tip ->
                Row(horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm), verticalAlignment = Alignment.Top) {
                    Text(text = "•", style = MaterialTheme.typography.bodyMedium, color = colors.warning)
                    Text(text = tip, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                }
            }
        }
    }
}

private fun buildTips(summary: AnalyticsSummary): List<String> {
    val tips = mutableListOf<String>()
    val total = summary.totalSafeDays + summary.totalOverspendDays
    val safeRate = if (total > 0) summary.totalSafeDays.toFloat() / total else 0f
    if (safeRate < 0.5f) tips.add("Try setting a small daily spending limit to reduce impulsive buys.")
    if (summary.currentStreak < 3) tips.add("Focus on building a 3-day streak first — small wins build big habits!")
    if (summary.averageResilienceScore < 50) tips.add("Log your resilience score daily to better understand your spending patterns.")
    if (summary.longestStreak >= 7) tips.add("You achieved a ${summary.longestStreak}-day streak! Try to beat it.")
    if (tips.isEmpty()) {
        tips.add("Excellent discipline! Keep maintaining your safe day habits.")
        tips.add("Consider sharing your progress with an accountability partner.")
    }
    return tips
}
