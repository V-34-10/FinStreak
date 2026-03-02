package com.finance.finstreak.ui.screen.resilience

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.ui.components.FinStreakTopBar
import com.finance.finstreak.ui.components.SectionHeader
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ResilienceCalculatorScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ResilienceCalculatorViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    LaunchedEffect(state.isSaved) { if (state.isSaved) onSaved() }
    LaunchedEffect(state.error) { state.error?.let { snackbarHostState.showSnackbar(it) } }

    val score = state.scoreText.toIntOrNull() ?: 0
    val scoreColor = when {
        score >= 80 -> colors.primary
        score >= 50 -> colors.warning
        else -> colors.error
    }
    val scoreLabel = when {
        score >= 80 -> stringResource(R.string.resilience_label_excellent)
        score >= 60 -> stringResource(R.string.resilience_label_good)
        score >= 40 -> stringResource(R.string.resilience_label_fair)
        else -> stringResource(R.string.resilience_label_needs_work)
    }

    Scaffold(
        topBar = { FinStreakTopBar(title = stringResource(R.string.resilience_title), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimens.spacingMd)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingXl - dimens.spacingXs)
        ) {
            ResilienceScoreVisual(score = score, scoreColor = scoreColor, label = scoreLabel)

            Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                SectionHeader(stringResource(R.string.resilience_section_score))
                OutlinedTextField(
                    value = state.scoreText,
                    onValueChange = { viewModel.onScoreChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.resilience_score_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(dimens.radiusMd),
                    isError = state.scoreError != null,
                    supportingText = {
                        state.scoreError?.let { Text(it, color = colors.error) }
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                SectionHeader(stringResource(R.string.resilience_section_criteria))
                Text(text = stringResource(R.string.resilience_criteria_subtitle), style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                OutlinedTextField(
                    value = state.criteriaNote,
                    onValueChange = { viewModel.onCriteriaNoteChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.resilience_criteria_placeholder)) },
                    minLines = 3,
                    shape = RoundedCornerShape(dimens.radiusMd),
                    isError = state.criteriaError != null,
                    supportingText = {
                        val err = state.criteriaError
                        if (err != null) Text(err, color = colors.error)
                        else Text("${state.criteriaNote.length}/200", color = colors.textSecondary)
                    }
                )
            }

            ScoreGuideCard()

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth().height(dimens.buttonHeight),
                shape = RoundedCornerShape(dimens.radiusMd),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                enabled = !state.isSaving
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = colors.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.resilience_btn_save), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        }
    }
}

@Composable
private fun ResilienceScoreVisual(score: Int, scoreColor: Color, label: String) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val animatedProgress by animateFloatAsState(targetValue = score / 100f, animationSpec = tween(600), label = "progress")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusLg),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(dimens.spacingLg), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$score", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold, color = scoreColor))
            Text(text = label, style = MaterialTheme.typography.titleMedium, color = scoreColor)
            Spacer(modifier = Modifier.height(dimens.spacingMd))
            Box(
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(colors.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxWidth(animatedProgress).height(12.dp).clip(RoundedCornerShape(6.dp)).background(scoreColor))
            }
        }
    }
}

@Composable
private fun ScoreGuideCard() {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            Text(text = stringResource(R.string.resilience_guide_title), style = MaterialTheme.typography.titleMedium)
            ScoreGuideRow("80–100", stringResource(R.string.resilience_label_excellent), colors.primary)
            ScoreGuideRow("60–79", stringResource(R.string.resilience_label_good), colors.warning)
            ScoreGuideRow("40–59", stringResource(R.string.resilience_label_fair), colors.warning)
            ScoreGuideRow("0–39", stringResource(R.string.resilience_label_needs_work), colors.error)
        }
    }
}

@Composable
private fun ScoreGuideRow(range: String, label: String, color: Color) {
    val colors = LocalAppTheme.colors
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = range, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        Text(text = label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold, color = color))
    }
}
