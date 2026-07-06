package com.roozbehzarei.filester.presentation.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AboutViewModel(private val userPreferencesRepository: UserPreferencesRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow((AboutUiState()))
    val uiState = _uiState.asStateFlow()

    init {
        getThemeMode()
    }

    private fun getThemeMode() {
        viewModelScope.launch {
            userPreferencesRepository.getThemePreference().collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

}