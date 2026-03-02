package com.finance.finstreak.ui.screen.addday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.data.model.DayStatus
import com.finance.finstreak.ui.components.FinStreakTopBar
import com.finance.finstreak.ui.components.SelectableOptionCard
import com.finance.finstreak.ui.components.SectionHeader
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddDayScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: AddDayViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    LaunchedEffect(state.isSaved) { if (state.isSaved) onSaved() }
    LaunchedEffect(state.error) { state.error?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        topBar = { FinStreakTopBar(title = stringResource(R.string.add_day_title), onBack = onBack) },
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
            val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
            Text(
                text = LocalDate.now().format(dateFormatter),
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            if (state.alreadyLoggedToday) {
                Text(
                    text = stringResource(R.string.add_day_already_logged),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                SectionHeader(stringResource(R.string.add_day_section_how))

                SelectableOptionCard(
                    label = stringResource(R.string.add_day_safe_label),
                    description = stringResource(R.string.add_day_safe_desc),
                    selected = state.selectedStatus == DayStatus.SAFE,
                    accentColor = colors.primary,
                    onClick = { viewModel.onStatusSelected(DayStatus.SAFE) }
                )

                SelectableOptionCard(
                    label = stringResource(R.string.add_day_overspend_label),
                    description = stringResource(R.string.add_day_overspend_desc),
                    selected = state.selectedStatus == DayStatus.OVERSPEND,
                    accentColor = colors.error,
                    onClick = { viewModel.onStatusSelected(DayStatus.OVERSPEND) }
                )

                state.statusError?.let { error ->
                    Text(text = error, style = MaterialTheme.typography.bodySmall, color = colors.error)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingXs)) {
                SectionHeader(stringResource(R.string.add_day_section_note))
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.onNoteChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.add_day_note_placeholder)) },
                    minLines = 3,
                    maxLines = 5,
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
                    Text(stringResource(R.string.add_day_btn_save), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        }
    }
}
