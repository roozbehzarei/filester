package com.roozbehzarei.filester.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define Preferences DataStore extension function
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepositoryImpl(private val context: Context) : UserPreferencesRepository {

    private companion object {
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("is_dynamic_colors")
        val THEME_KEY = intPreferencesKey("theme_mode")
        val TELEMETRY_KEY = booleanPreferencesKey("telemetry")
        val HOST_PROVIDER_KEY = stringPreferencesKey("host_provider")
    }

    override fun getDynamicColorsPreference(): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DYNAMIC_COLOR_KEY] == true
        }

    override fun getThemePreference(): Flow<Theme> = context.dataStore.data.map { preferences ->
        Theme.fromIndexOrDefault(preferences[THEME_KEY])
    }

    override fun getTelemetryPreference(): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[TELEMETRY_KEY] == true
        }

    override fun getHostProviderPreference(): Flow<HostProvider> =
        context.dataStore.data.map { preferences ->
            val name = preferences[HOST_PROVIDER_KEY]
            runCatching { HostProvider.valueOf(name.orEmpty()) }.getOrDefault(HostProvider.CATBOX)
        }

    override suspend fun saveDynamicColorsPreference(isDynamic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = isDynamic
        }
    }

    override suspend fun saveThemePreference(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.index
        }
    }

    override suspend fun saveTelemetryPreference(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TELEMETRY_KEY] = isEnabled
        }
    }

    override suspend fun saveHostProviderPreference(hostProvider: HostProvider) {
        context.dataStore.edit { preferences ->
            preferences[HOST_PROVIDER_KEY] = hostProvider.name
        }
    }

}