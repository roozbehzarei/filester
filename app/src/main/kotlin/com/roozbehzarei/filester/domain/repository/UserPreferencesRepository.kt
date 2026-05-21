package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    fun getDynamicColorsPreference(): Flow<Boolean>

    fun getThemePreference(): Flow<Theme>

    fun getTelemetryPreference(): Flow<Boolean>

    fun getCrashReportPreference(): Flow<Boolean>

    suspend fun saveDynamicColorsPreference(isDynamic: Boolean)

    suspend fun saveThemePreference(theme: Theme)

    suspend fun saveTelemetryPreference(isEnabled: Boolean)

    suspend fun saveCrashReportPreference(isEnabled: Boolean)

}