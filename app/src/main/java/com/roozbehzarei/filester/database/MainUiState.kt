package com.roozbehzarei.filester.database

import com.roozbehzarei.filester.model.FilesterConfig

data class MainUiState(
    val appConfig: FilesterConfig? = null,
    val isFileDeleted: Boolean = false
)