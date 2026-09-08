package com.roozbehzarei.filester.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> =
        combine(
            userPreferencesRepository.getDynamicColorsPreference(),
            userPreferencesRepository.getThemePreference(),
        ) { isDynamicColor, theme ->
            MainUiState(
                isDynamicColor = isDynamicColor,
                theme = theme,
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainUiState(isLoading = true, isDynamicColor = false, theme = Theme.Default),
        )
}
