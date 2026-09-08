package com.roozbehzarei.filester.presentation.main

import com.roozbehzarei.filester.domain.model.Theme

data class MainUiState(
    val isLoading: Boolean,
    val isDynamicColor: Boolean,
    val theme: Theme,
)
