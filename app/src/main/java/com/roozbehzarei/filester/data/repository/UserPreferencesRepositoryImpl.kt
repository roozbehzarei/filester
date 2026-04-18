package com.roozbehzarei.filester.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define Preferences DataStore extension function
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepositoryImpl(private val context: Context) : UserPreferencesRepository {

    private companion object {
        val IS_DYNAMIC_COLORS = booleanPreferencesKey("is_dynamic_colors")
        val THEME_MODE = intPreferencesKey("theme_mode")
        val TELEMETRY = booleanPreferencesKey("telemetry")
        val CRASH_REPORT = booleanPreferencesKey("crash_report")
    }

    override fun getDynamicColorsPreference(): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[IS_DYNAMIC_COLORS] == true
        }

    override fun getThemePreference(): Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: 1
    }

    override fun getTelemetryPreference(): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[TELEMETRY] == true
        }

    override fun getCrashReportPreference(): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[CRASH_REPORT] != false
        }

    override suspend fun saveDynamicColorsPreference(isDynamic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLORS] = isDynamic
        }
    }

    override suspend fun saveThemeModePreference(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = index
        }
    }

    override suspend fun saveTelemetryPreference(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TELEMETRY] = isEnabled
        }
    }

    override suspend fun saveCrashReportPreference(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CRASH_REPORT] = isEnabled
        }
    }

}