package com.finance.finstreak.ui.screen.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.BuildConfig
import com.finance.finstreak.R
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigate: (String) -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }
    LaunchedEffect(state.clearSuccess) {
        if (state.clearSuccess) {
            snackbarHostState.showSnackbar(context.getString(R.string.settings_clear_success))
            viewModel.clearSuccessFlag()
        }
    }

    if (state.showClearDataConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearDataConfirm() },
            title = { Text(stringResource(R.string.settings_clear_data_dialog_title)) },
            text = { Text(stringResource(R.string.settings_clear_data_dialog_body)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearAllData() },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.error)
                ) { Text(stringResource(R.string.settings_clear_data_title)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissClearDataConfirm() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (state.showResetConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissResetConfirm() },
            title = { Text(stringResource(R.string.settings_reset_dialog_title)) },
            text = { Text(stringResource(R.string.settings_reset_dialog_body)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetAllSettings() },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.error)
                ) { Text(stringResource(R.string.settings_reset_title)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissResetConfirm() }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(dimens.spacingMd),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)
        ) {
            item { SettingsSectionHeader(stringResource(R.string.settings_section_preferences)) }

            item {
                SettingsCard {
                    SettingsToggleItem(
                        icon = Icons.Filled.Notifications,
                        title = stringResource(R.string.settings_notifications_title),
                        subtitle = stringResource(R.string.settings_notifications_subtitle),
                        checked = state.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications(it) }
                    )
                }
            }

            item { SettingsSectionHeader(stringResource(R.string.settings_section_data)) }

            item {
                SettingsCard {
                    SettingsActionItem(
                        icon = Icons.Filled.Delete,
                        title = stringResource(R.string.settings_clear_data_title),
                        subtitle = stringResource(R.string.settings_clear_data_subtitle),
                        iconTint = colors.error,
                        onClick = { viewModel.showClearDataConfirm() }
                    )
                    SettingsDivider()
                    SettingsActionItem(
                        icon = Icons.Filled.Refresh,
                        title = stringResource(R.string.settings_reset_title),
                        subtitle = stringResource(R.string.settings_reset_subtitle),
                        iconTint = colors.error,
                        onClick = { viewModel.showResetConfirm() }
                    )
                }
            }

            item { SettingsSectionHeader(stringResource(R.string.settings_section_about)) }

            item {
                SettingsCard {
                    SettingsActionItem(
                        icon = Icons.Filled.Star,
                        title = stringResource(R.string.settings_rate_title),
                        subtitle = stringResource(R.string.settings_rate_subtitle),
                        iconTint = colors.primary,
                        onClick = {
                            val marketUri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
                            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, marketUri))
                            } catch (_: Exception) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        }
                    )
                    SettingsDivider()
                    SettingsActionItem(
                        icon = Icons.Filled.Share,
                        title = stringResource(R.string.settings_share_title),
                        subtitle = stringResource(R.string.settings_share_subtitle),
                        iconTint = colors.primary,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.settings_share_subject))
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${context.getString(R.string.settings_share_text)} " +
                                        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(intent, context.getString(R.string.settings_share_title))
                            )
                        }
                    )
                    SettingsDivider()
                    SettingsActionItem(
                        icon = Icons.Filled.Policy,
                        title = stringResource(R.string.settings_privacy_title),
                        subtitle = stringResource(R.string.settings_privacy_subtitle),
                        iconTint = colors.textSecondary,
                        onClick = {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://finstreak.app/privacy"))
                            )
                        }
                    )
                    SettingsDivider()
                    SettingsInfoItem(
                        icon = Icons.Filled.Info,
                        title = stringResource(R.string.settings_version_title),
                        value = BuildConfig.VERSION_NAME
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = colors.primary,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = Modifier.padding(vertical = dimens.spacingXs, horizontal = dimens.spacingXs)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column { content() }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 56.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(dimens.iconSizeMd)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary
            )
        )
    }
}

@Composable
private fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(dimens.iconSizeMd)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.spacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(dimens.iconSizeMd)
        )
        Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
    }
}
