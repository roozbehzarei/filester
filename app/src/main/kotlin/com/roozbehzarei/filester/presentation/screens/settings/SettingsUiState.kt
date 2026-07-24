package com.roozbehzarei.filester.presentation.screens.settings

import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.Theme

data class SettingsUiState(
    val themeMode: Theme,
    val isDynamicColor: Boolean,
    val isTelemetryEnabled: Boolean,
    val hostProvider: HostProvider
)