package com.roozbehzarei.filester.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepository {

    private companion object {
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("is_dynamic_colors")
        val THEME_KEY = intPreferencesKey("theme_mode")
        val TELEMETRY_KEY = booleanPreferencesKey("telemetry")
        val HOST_PROVIDER_KEY = stringPreferencesKey("host_provider")
    }

    override fun getDynamicColorsPreference(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] == true
    }

    override fun getThemePreference(): Flow<Theme> = dataStore.data.map { preferences ->
        Theme.fromIndexOrDefault(preferences[THEME_KEY])
    }

    override fun getTelemetryPreference(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[TELEMETRY_KEY] == true
    }

    override fun getHostProviderPreference(): Flow<HostProvider> =
        dataStore.data.map { preferences ->
            HostProvider.fromId(preferences[HOST_PROVIDER_KEY]) ?: HostProvider.LITTERBOX
        }

    override suspend fun saveDynamicColorsPreference(isDynamic: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = isDynamic
        }
    }

    override suspend fun saveThemePreference(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.index
        }
    }

    override suspend fun saveTelemetryPreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[TELEMETRY_KEY] = isEnabled
        }
    }

    override suspend fun saveHostProviderPreference(hostProvider: HostProvider) {
        dataStore.edit { preferences ->
            preferences[HOST_PROVIDER_KEY] = hostProvider.id
        }
    }

}