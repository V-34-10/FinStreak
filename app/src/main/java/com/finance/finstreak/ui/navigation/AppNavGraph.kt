package com.finance.finstreak.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.finance.finstreak.ui.screen.addday.AddDayScreen
import com.finance.finstreak.ui.screen.analytics.AnalyticsScreen
import com.finance.finstreak.ui.screen.daydetail.DayDetailScreen
import com.finance.finstreak.ui.screen.editday.EditDayScreen
import com.finance.finstreak.ui.screen.history.HistoryScreen
import com.finance.finstreak.ui.screen.home.HomeScreen
import com.finance.finstreak.ui.screen.onboarding.OnboardingScreen1
import com.finance.finstreak.ui.screen.onboarding.OnboardingScreen2
import com.finance.finstreak.ui.screen.preloader.PreloaderScreen
import com.finance.finstreak.ui.screen.resilience.ResilienceCalculatorScreen
import com.finance.finstreak.ui.screen.settings.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.PRELOADER,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        composable(NavRoutes.PRELOADER) {
            PreloaderScreen(
                onNavigateToOnboarding = {
                    navController.navigate(NavRoutes.ONBOARDING_1) {
                        popUpTo(NavRoutes.PRELOADER) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.PRELOADER) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.ONBOARDING_1) {
            OnboardingScreen1(
                onNext = { navController.navigate(NavRoutes.ONBOARDING_2) }
            )
        }

        composable(NavRoutes.ONBOARDING_2) {
            OnboardingScreen2(
                onFinish = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.ONBOARDING_1) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME) {
            HomeScreen(
                onAddDay = { navController.navigate(NavRoutes.ADD_DAY) },
                onViewHistory = { navController.navigate(NavRoutes.HISTORY) },
                onViewAnalytics = { navController.navigate(NavRoutes.ANALYTICS) },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_DAY) {
            AddDayScreen(
                onSaved = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.RESILIENCE_CALCULATOR) {
            ResilienceCalculatorScreen(
                onSaved = { navController.navigate(NavRoutes.ANALYTICS) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.HISTORY) {
            HistoryScreen(
                onDaySelected = { dayId -> navController.navigate(NavRoutes.dayDetail(dayId)) },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = NavRoutes.DAY_DETAIL,
            arguments = listOf(navArgument("dayId") { type = NavType.LongType })
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getLong("dayId") ?: 0L
            DayDetailScreen(
                dayId = dayId,
                onEdit = { navController.navigate(NavRoutes.editDay(dayId)) },
                onDeleted = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.EDIT_DAY,
            arguments = listOf(navArgument("dayId") { type = NavType.LongType })
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getLong("dayId") ?: 0L
            EditDayScreen(
                dayId = dayId,
                onSaved = { navController.popBackStack() },
                onDeleted = {
                    navController.navigate(NavRoutes.HISTORY) {
                        popUpTo(NavRoutes.HISTORY) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ANALYTICS) {
            AnalyticsScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}
