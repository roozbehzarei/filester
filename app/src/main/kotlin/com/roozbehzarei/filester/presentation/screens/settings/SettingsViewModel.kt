package com.roozbehzarei.filester.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = with(userPreferencesRepository) {
        combine(
            getDynamicColorsPreference(),
            getThemePreference(),
            getTelemetryPreference()
        ) { isDynamicColor, themeMode, isTelemetryEnabled ->
            SettingsUiState(
                themeMode = themeMode,
                isDynamicColor = isDynamicColor,
                isTelemetryEnabled = isTelemetryEnabled
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(
                themeMode = Theme.Default,
                isDynamicColor = false,
                isTelemetryEnabled = false
            )
        )
    }

    fun saveDynamicColorPref(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveDynamicColorsPreference(enabled)
        }
    }

    fun saveThemeModePref(theme: Theme) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(theme)
        }
    }

    fun saveTelemetryPref(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveTelemetryPreference(enabled)
        }
    }
}
