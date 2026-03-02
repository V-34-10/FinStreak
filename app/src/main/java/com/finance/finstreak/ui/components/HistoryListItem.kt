package com.finance.finstreak.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.data.model.DayEntry
import com.finance.finstreak.data.model.DayStatus
import com.finance.finstreak.ui.theme.LocalAppTheme
import java.time.format.DateTimeFormatter

@Composable
fun HistoryListItem(entry: DayEntry, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    val isSafe = entry.status == DayStatus.SAFE
    val statusColor = if (isSafe) colors.primary else colors.error
    val statusIcon = if (isSafe) Icons.Filled.CheckCircle else Icons.Filled.Warning
    val statusLabel = if (isSafe) stringResource(R.string.day_detail_safe_label) else stringResource(R.string.day_detail_overspend_label)
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")

    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(dimens.radiusMd),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(dimens.spacingMd).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = statusIcon, contentDescription = statusLabel, tint = statusColor, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.width(dimens.spacingSm))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
                    Text(text = entry.date.format(formatter), style = MaterialTheme.typography.titleMedium)
                    StatusBadge(label = statusLabel, color = statusColor)
                }
                if (entry.note.isNotBlank()) {
                    Text(text = entry.note, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, maxLines = 1)
                }
                if (entry.resilienceScore != null) {
                    Text(text = "Resilience: ${entry.resilienceScore}/100", style = MaterialTheme.typography.labelSmall, color = colors.textSecondary)
                }
            }

            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(dimens.iconSizeSm))
        }
    }
}

@Composable
fun StatusBadge(label: String, color: androidx.compose.ui.graphics.Color) {
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 2.dp), contentAlignment = Alignment.Center) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = color))
    }
}
