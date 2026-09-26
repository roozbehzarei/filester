/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.presentation.screens.settings

import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.Theme

data class SettingsUiState(
    val themeMode: Theme,
    val isDynamicColor: Boolean,
    val isTelemetryEnabled: Boolean,
    val isMonitoringEnabled: Boolean,
    val hostProvider: HostProvider,
)
