package com.finance.finstreak.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        private val KEY_NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val KEY_NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val KEY_APP_VERSION_REVIEWED = stringPreferencesKey("app_version_reviewed")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_ONBOARDING_COMPLETED] ?: false }

    val isNotificationEnabled: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATION_ENABLED] ?: true }

    val notificationHour: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATION_HOUR] ?: 20 }

    val notificationMinute: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATION_MINUTE] ?: 0 }

    val appVersionReviewed: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_APP_VERSION_REVIEWED] ?: "" }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATION_HOUR] = hour
            prefs[KEY_NOTIFICATION_MINUTE] = minute
        }
    }

    suspend fun setAppVersionReviewed(version: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_APP_VERSION_REVIEWED] = version
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
