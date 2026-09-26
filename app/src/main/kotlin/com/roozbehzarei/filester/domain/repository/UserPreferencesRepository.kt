/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getDynamicColorsPreference(): Flow<Boolean>

    fun getThemePreference(): Flow<Theme>

    fun getTelemetryPreference(): Flow<Boolean>

    fun getHostProviderPreference(): Flow<HostProvider>

    suspend fun saveDynamicColorsPreference(isDynamic: Boolean)

    suspend fun saveThemePreference(theme: Theme)

    suspend fun saveTelemetryPreference(isEnabled: Boolean)

    suspend fun saveHostProviderPreference(hostProvider: HostProvider)
}
