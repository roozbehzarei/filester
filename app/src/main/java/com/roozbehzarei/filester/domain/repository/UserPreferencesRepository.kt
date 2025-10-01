package com.roozbehzarei.filester.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    fun getDynamicColorsPreference(): Flow<Boolean>

    fun getThemePreference(): Flow<Int>

    fun getTelemetryPreference(): Flow<Boolean>

    fun getCrashReportPreference(): Flow<Boolean>

    suspend fun saveDynamicColorsPreference(isDynamic: Boolean)

    suspend fun saveThemeModePreference(index: Int)

    suspend fun saveTelemetryPreference(isEnabled: Boolean)

    suspend fun saveCrashReportPreference(isEnabled: Boolean)

}