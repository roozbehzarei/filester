package com.roozbehzarei.filester.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

// Define Preferences DataStore extension function
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Single
class UserPreferencesRepository(private val context: Context) {

    private companion object {
        val IS_DYNAMIC_COLORS = booleanPreferencesKey("is_dynamic_colors")
        val THEME_MODE = intPreferencesKey("theme_mode")
    }

    val getDynamicColorsPreference: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DYNAMIC_COLORS] == true
    }

    val getThemePreference: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: 1
    }

    suspend fun saveDynamicColorsPreference(isDynamic: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DYNAMIC_COLORS] = isDynamic
        }
    }

    suspend fun saveThemeModePreference(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = index
        }
    }

}