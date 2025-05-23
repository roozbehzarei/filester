package com.roozbehzarei.filester.presentation.screens.main

import androidx.work.WorkInfo
import com.roozbehzarei.filester.domain.model.File

data class MainUiState(
    val files: List<File> = listOf(),
    val isFileDeleted: Boolean = false,
    val uploadStatus: WorkInfo.State? = null,
    val uploadProgress: Int? = null,
    val uploadingFileName: String? = null
)