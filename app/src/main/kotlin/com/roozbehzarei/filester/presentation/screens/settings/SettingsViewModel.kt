package com.roozbehzarei.filester.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import com.roozbehzarei.filester.getMonitoringConsent
import com.roozbehzarei.filester.setMonitoringConsent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    // The monitoring SDK persists consent itself, so it is the source of truth rather than a
    // preference of our own; this only mirrors it so the switch can recompose.
    private val monitoringConsent = MutableStateFlow(getMonitoringConsent())

    val uiState: StateFlow<SettingsUiState> =
        with(userPreferencesRepository) {
            combine(
                getDynamicColorsPreference(),
                getThemePreference(),
                getTelemetryPreference(),
                monitoringConsent,
                getHostProviderPreference(),
            ) { isDynamicColor, themeMode, isTelemetryEnabled, isMonitoringEnabled, hostProvider ->
                SettingsUiState(
                    themeMode = themeMode,
                    isDynamicColor = isDynamicColor,
                    isTelemetryEnabled = isTelemetryEnabled,
                    isMonitoringEnabled = isMonitoringEnabled,
                    hostProvider = hostProvider,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue =
                    SettingsUiState(
                        themeMode = Theme.Default,
                        isDynamicColor = false,
                        isTelemetryEnabled = false,
                        isMonitoringEnabled = monitoringConsent.value,
                        hostProvider = HostProvider.LITTERBOX,
                    ),
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

    fun saveMonitoringPref(enabled: Boolean) {
        // Off the main thread: recording consent flushes to storage synchronously.
        viewModelScope
            .launch(Dispatchers.IO) {
                setMonitoringConsent(enabled)
            }.invokeOnCompletion {
                monitoringConsent.value = getMonitoringConsent()
            }
    }

    fun saveHostProviderPref(hostProvider: HostProvider) {
        viewModelScope.launch {
            userPreferencesRepository.saveHostProviderPreference(hostProvider)
        }
    }
}
