package com.finance.finstreak.ui.screen.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finance.finstreak.R
import com.finance.finstreak.ui.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen2(onFinish: () -> Unit) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(dimens.spacingLg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(dimens.spacingXl))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colors.secondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = colors.secondary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacingXl))

            Text(
                text = stringResource(R.string.onboarding2_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimens.spacingMd))

            Text(
                text = stringResource(R.string.onboarding2_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = colors.textSecondary
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(dimens.spacingSm)) {
            OnboardingFeatureCard2(
                icon = Icons.Filled.Edit,
                title = stringResource(R.string.onboarding2_feature1_title),
                description = stringResource(R.string.onboarding2_feature1_desc),
                accentColor = colors.primary
            )
            OnboardingFeatureCard2(
                icon = Icons.Filled.Shield,
                title = stringResource(R.string.onboarding2_feature2_title),
                description = stringResource(R.string.onboarding2_feature2_desc),
                accentColor = colors.secondary
            )
            OnboardingFeatureCard2(
                icon = Icons.Filled.BarChart,
                title = stringResource(R.string.onboarding2_feature3_title),
                description = stringResource(R.string.onboarding2_feature3_desc),
                accentColor = colors.primary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(colors.border)
                )
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.primary)
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacingLg))

            Button(
                onClick = {
                    viewModel.completeOnboarding()
                    onFinish()
                },
                modifier = Modifier.fillMaxWidth().height(dimens.buttonHeight),
                shape = RoundedCornerShape(dimens.radiusMd),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text(
                    text = stringResource(R.string.onboarding2_btn_get_started),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacingMd))
        }
    }
}

@Composable
private fun OnboardingFeatureCard2(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    val colors = LocalAppTheme.colors
    val dimens = LocalAppTheme.dimens
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(dimens.radiusMd),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spacingMd)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
            }
        }
    }
}
