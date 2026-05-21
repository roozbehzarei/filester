package com.roozbehzarei.filester.presentation.screens.main

import androidx.work.WorkInfo
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.presentation.UiText

data class MainUiState(
    val message: UiText? = null,
    val files: List<File> = listOf(),
    val uploadStatus: WorkInfo.State? = null,
    val uploadProgress: Int? = null,
    val uploadingFileName: String? = null
)