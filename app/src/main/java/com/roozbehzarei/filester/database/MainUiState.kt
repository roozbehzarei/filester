package com.roozbehzarei.filester.database

import com.roozbehzarei.filester.model.Version

data class MainUiState(
    val appVersion: Version? = null,
    val isFileDeleted: Boolean = false
)