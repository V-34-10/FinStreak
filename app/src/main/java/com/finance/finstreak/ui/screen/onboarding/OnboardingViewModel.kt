package com.finance.finstreak.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.finstreak.data.datastore.UserPreferencesDataStore
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    fun completeOnboarding() {
        viewModelScope.launch {
            prefs.setOnboardingCompleted(true)
        }
    }
}
