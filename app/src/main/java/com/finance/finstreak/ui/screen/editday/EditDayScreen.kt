package com.finance.finstreak.ui.screen.editday

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.data.model.DayStatus
import com.finance.finstreak.ui.components.FinStreakTopBar
import com.finance.finstreak.ui.components.SelectableOptionCard
import com.finance.finstreak.ui.components.SectionHeader
import com.finance.finstreak.ui.components.SkeletonCard
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun EditDayScreen(
    dayId: Long,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: EditDayViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    LaunchedEffect(dayId) { viewModel.loadEntry(dayId) }
    LaunchedEffect(state.isSaved) { if (state.isSaved) onSaved() }
    LaunchedEffect(state.isDeleted) { if (state.isDeleted) onDeleted() }
    LaunchedEffect(state.error) { state.error?.let { snackbarHostState.showSnackbar(it) } }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirm() },
            title = { Text(stringResource(R.string.edit_day_delete_dialog_title)) },
            text = { Text(stringResource(R.string.edit_day_delete_dialog_body)) },
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
        topBar = { FinStreakTopBar(title = stringResource(R.string.edit_day_title), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(modifier = Modifier.padding(paddingValues).padding(dimens.spacingMd), verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                repeat(3) { SkeletonCard() }
            }
            return@Scaffold
        }

        val entry = state.entry
        if (entry == null) {
            Text(text = stringResource(R.string.day_detail_not_found_title), modifier = Modifier.padding(paddingValues).padding(dimens.spacingMd))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimens.spacingMd)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingXl - dimens.spacingXs)
        ) {
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            Text(text = entry.date.format(formatter), style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)

            Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                SectionHeader(stringResource(R.string.edit_day_section_status))

                SelectableOptionCard(
                    label = stringResource(R.string.add_day_safe_label),
                    description = stringResource(R.string.edit_day_safe_desc),
                    selected = state.selectedStatus == DayStatus.SAFE,
                    accentColor = colors.primary,
                    onClick = { viewModel.onStatusChanged(DayStatus.SAFE) }
                )

                SelectableOptionCard(
                    label = stringResource(R.string.add_day_overspend_label),
                    description = stringResource(R.string.edit_day_overspend_desc),
                    selected = state.selectedStatus == DayStatus.OVERSPEND,
                    accentColor = colors.error,
                    onClick = { viewModel.onStatusChanged(DayStatus.OVERSPEND) }
                )

                state.statusError?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = colors.error) }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingXs)) {
                SectionHeader(stringResource(R.string.edit_day_section_note))
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.onNoteChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.edit_day_note_placeholder)) },
                    minLines = 3,
                    shape = RoundedCornerShape(dimens.radiusMd),
                    isError = state.noteError != null,
                    supportingText = {
                        val err = state.noteError
                        if (err != null) Text(err, color = colors.error)
                        else Text("${state.note.length}/200", color = colors.textSecondary)
                    }
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacingSm))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                OutlinedButton(
                    onClick = { viewModel.showDeleteConfirm() },
                    modifier = Modifier.weight(1f).height(dimens.buttonHeight),
                    shape = RoundedCornerShape(dimens.radiusMd)
                ) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, tint = colors.error)
                    Spacer(modifier = Modifier.size(dimens.spacingXs))
                    Text(stringResource(R.string.action_delete), color = colors.error)
                }

                Button(
                    onClick = { viewModel.saveEdit() },
                    modifier = Modifier.weight(1f).height(dimens.buttonHeight),
                    shape = RoundedCornerShape(dimens.radiusMd),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), color = colors.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.edit_day_btn_save), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }
        }
    }
}
