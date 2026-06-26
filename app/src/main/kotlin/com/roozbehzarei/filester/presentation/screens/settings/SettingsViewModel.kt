package com.roozbehzarei.filester.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roozbehzarei.filester.data.repository.UserPreferencesRepositoryImpl
import com.roozbehzarei.filester.domain.model.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepositoryImpl
) : ViewModel() {
    val getDynamicColorPref = userPreferencesRepository.getDynamicColorsPreference()
    val getThemeModePref = userPreferencesRepository.getThemePreference()
    val getTelemetryPref = userPreferencesRepository.getTelemetryPreference()
    val getCrashReportPref = userPreferencesRepository.getCrashReportPreference()

    fun saveDynamicColorPref(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userPreferencesRepository.saveDynamicColorsPreference(enabled)
            }
        }
    }

    fun saveThemeModePref(theme: Theme) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userPreferencesRepository.saveThemePreference(theme)
            }
        }
    }

    fun saveTelemetryPref(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userPreferencesRepository.saveTelemetryPreference(enabled)
            }
        }
    }

    fun saveCrashReportPref(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userPreferencesRepository.saveCrashReportPreference(enabled)
            }
        }
    }

}