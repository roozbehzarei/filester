package com.roozbehzarei.filester.ui.screens.main

import androidx.work.WorkInfo
import com.roozbehzarei.filester.data.local.File

data class MainUiState(
    val files: List<File> = listOf(),
    val isFileDeleted: Boolean = false,
    val uploadStatus: WorkInfo.State? = null,
    val uploadingFileName: String? = null
)