package com.finance.finstreak.di

import androidx.room.Room
import com.finance.finstreak.data.datastore.UserPreferencesDataStore
import com.finance.finstreak.data.db.FinStreakDatabase
import com.finance.finstreak.data.repository.DayEntryRepository
import com.finance.finstreak.ui.screen.addday.AddDayViewModel
import com.finance.finstreak.ui.screen.analytics.AnalyticsViewModel
import com.finance.finstreak.ui.screen.daydetail.DayDetailViewModel
import com.finance.finstreak.ui.screen.editday.EditDayViewModel
import com.finance.finstreak.ui.screen.history.HistoryViewModel
import com.finance.finstreak.ui.screen.home.HomeViewModel
import com.finance.finstreak.ui.screen.onboarding.OnboardingViewModel
import com.finance.finstreak.ui.screen.resilience.ResilienceCalculatorViewModel
import com.finance.finstreak.ui.screen.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            FinStreakDatabase::class.java,
            FinStreakDatabase.DATABASE_NAME
        ).build()
    }
    single { get<FinStreakDatabase>().dayEntryDao() }
}

val repositoryModule = module {
    single { DayEntryRepository(get()) }
    single { UserPreferencesDataStore(androidContext()) }
}

val viewModelModule = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AddDayViewModel(get()) }
    viewModel { ResilienceCalculatorViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { DayDetailViewModel(get()) }
    viewModel { EditDayViewModel(get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { SettingsViewModel(get(), get(), androidContext()) }
}

val appModules = listOf(databaseModule, repositoryModule, viewModelModule)
