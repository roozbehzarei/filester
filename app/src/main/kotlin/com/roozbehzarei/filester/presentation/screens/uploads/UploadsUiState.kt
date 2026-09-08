package com.roozbehzarei.filester.presentation.screens.uploads

import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.presentation.UiText
import com.roozbehzarei.filester.upload.UploadState
import com.roozbehzarei.filester.upload.UploadStatus

data class UploadsUiState(
    val message: UiText? = null,
    val files: List<File> = listOf(),
    val uploadStatus: UploadStatus = UploadStatus(UploadState.INACTIVE, 0),
    val uploadingFileName: String? = null,
)
