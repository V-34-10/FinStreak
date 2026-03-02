package com.finance.finstreak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finance.finstreak.ui.navigation.AppBottomNavBar
import com.finance.finstreak.ui.navigation.AppNavGraph
import com.finance.finstreak.ui.navigation.bottomNavRoutes
import com.finance.finstreak.ui.theme.FinStreakTheme
import com.finance.finstreak.util.AppLifecycleObserver
import com.finance.finstreak.util.scheduleDailyReminder

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycle.addObserver(AppLifecycleObserver())
        scheduleDailyReminder(this)

        setContent {
            FinStreakTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in bottomNavRoutes

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            AppBottomNavBar(navController = navController)
                        }
                    }
                ) { _ ->
                    AppNavGraph(
                        navController = navController
                    )
                }
            }
        }
    }
}
