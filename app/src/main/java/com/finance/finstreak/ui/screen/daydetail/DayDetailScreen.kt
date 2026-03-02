package com.finance.finstreak.ui.screen.daydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.data.model.DayStatus
import com.finance.finstreak.ui.components.EmptyStateView
import com.finance.finstreak.ui.components.ErrorView
import com.finance.finstreak.ui.components.FinStreakTopBar
import com.finance.finstreak.ui.components.SkeletonCard
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun DayDetailScreen(
    dayId: Long,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: DayDetailViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    LaunchedEffect(dayId) { viewModel.loadEntry(dayId) }
    LaunchedEffect(state.isDeleted) { if (state.isDeleted) onDeleted() }
    LaunchedEffect(state.error) { state.error?.let { snackbarHostState.showSnackbar(it) } }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirm() },
            title = { Text(stringResource(R.string.day_detail_delete_dialog_title)) },
            text = { Text(stringResource(R.string.day_detail_delete_dialog_body)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteEntry() },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.error)
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirm() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            FinStreakTopBar(
                title = stringResource(R.string.day_detail_title),
                onBack = onBack,
                actions = {
                    if (state.entry != null) {
                        IconButton(onClick = onEdit) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit))
                        }
                        IconButton(onClick = { viewModel.showDeleteConfirm() }) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_delete), tint = colors.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Column(modifier = Modifier.padding(paddingValues).padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                    repeat(3) { SkeletonCard() }
                }
            }

            state.error != null && state.entry == null -> {
                ErrorView(message = state.error!!, onRetry = { viewModel.loadEntry(dayId) }, modifier = Modifier.fillMaxSize().padding(paddingValues))
            }

            state.entry == null -> {
                EmptyStateView(
                    emoji = "🔍",
                    title = stringResource(R.string.day_detail_not_found_title),
                    subtitle = stringResource(R.string.day_detail_not_found_subtitle),
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }

            else -> {
                val entry = state.entry!!
                val isSafe = entry.status == DayStatus.SAFE
                val statusColor = if (isSafe) colors.primary else colors.error
                val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(dimens.spacingMd)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingMd)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimens.radiusLg),
                        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier.padding(dimens.spacingLg - 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSafe) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                    contentDescription = if (isSafe) stringResource(R.string.cd_safe_status) else stringResource(R.string.cd_overspend_status),
                                    tint = statusColor,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isSafe) stringResource(R.string.day_detail_safe_label) else stringResource(R.string.day_detail_overspend_label),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = statusColor)
                                )
                                Text(
                                    text = entry.date.format(formatter),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }

                    if (entry.note.isNotBlank()) {
                        DetailSection(title = stringResource(R.string.day_detail_section_note)) {
                            Text(text = entry.note, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    if (entry.resilienceScore != null) {
                        DetailSection(title = stringResource(R.string.day_detail_section_resilience)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                                val scoreColor = when {
                                    entry.resilienceScore >= 80 -> colors.primary
                                    entry.resilienceScore >= 50 -> colors.warning
                                    else -> colors.error
                                }
                                Text(
                                    text = "${entry.resilienceScore}",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = scoreColor)
                                )
                                Text(text = "/ 100", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    if (entry.criteriaNote.isNotBlank()) {
                        DetailSection(title = stringResource(R.string.day_detail_section_criteria)) {
                            Text(text = entry.criteriaNote, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Spacer(modifier = Modifier.height(dimens.spacingSm))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                        OutlinedButton(
                            onClick = { viewModel.showDeleteConfirm() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(dimens.radiusMd)
                        ) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = colors.error)
                            Spacer(modifier = Modifier.size(dimens.spacingSm))
                            Text(stringResource(R.string.action_delete), color = colors.error)
                        }
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(dimens.radiusMd),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.size(dimens.spacingSm))
                            Text(stringResource(R.string.action_edit))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
            content()
        }
    }
}
