package com.finance.finstreak.ui.navigation

object NavRoutes {
    const val PRELOADER = "preloader"
    const val ONBOARDING_1 = "onboarding1"
    const val ONBOARDING_2 = "onboarding2"
    const val HOME = "home"
    const val ADD_DAY = "add_day"
    const val RESILIENCE_CALCULATOR = "resilience_calculator"
    const val HISTORY = "history"
    const val DAY_DETAIL = "day_detail/{dayId}"
    const val EDIT_DAY = "edit_day/{dayId}"
    const val ANALYTICS = "analytics"
    const val SETTINGS = "settings"

    fun dayDetail(dayId: Long) = "day_detail/$dayId"
    fun editDay(dayId: Long) = "edit_day/$dayId"
}

sealed class BottomNavItem(val route: String, val label: String, val iconResId: Int) {
    object Home : BottomNavItem(NavRoutes.HOME, "Home", 0)
    object History : BottomNavItem(NavRoutes.HISTORY, "History", 0)
    object Analytics : BottomNavItem(NavRoutes.ANALYTICS, "Analytics", 0)
    object Settings : BottomNavItem(NavRoutes.SETTINGS, "Settings", 0)
}
