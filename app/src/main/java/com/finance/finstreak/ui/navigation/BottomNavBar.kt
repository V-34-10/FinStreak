package com.finance.finstreak.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.finance.finstreak.R
import com.finance.finstreak.ui.theme.LocalAppTheme

data class BottomNavEntry(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int
)

val bottomNavItems = listOf(
    BottomNavEntry(NavRoutes.HOME, R.string.nav_home, R.drawable.ic_home),
    BottomNavEntry(NavRoutes.HISTORY, R.string.nav_history, R.drawable.ic_history),
    BottomNavEntry(NavRoutes.ANALYTICS, R.string.nav_analytics, R.drawable.ic_analytics),
    BottomNavEntry(NavRoutes.SETTINGS, R.string.nav_settings, R.drawable.ic_settings)
)

val bottomNavRoutes = setOf(NavRoutes.HOME, NavRoutes.HISTORY, NavRoutes.ANALYTICS, NavRoutes.SETTINGS)

@Composable
fun AppBottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val colors = LocalAppTheme.colors

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 2.dp
    ) {
        bottomNavItems.forEach { item ->
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(NavRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = label
                    )
                },
                label = { Text(text = label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.primary,
                    unselectedIconColor = colors.textSecondary,
                    unselectedTextColor = colors.textSecondary,
                    indicatorColor = colors.primaryLight
                )
            )
        }
    }
}
