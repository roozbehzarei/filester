/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

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
