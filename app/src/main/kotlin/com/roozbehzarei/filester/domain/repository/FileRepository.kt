/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.UploadResult
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    fun getFiles(): Flow<List<File>>

    fun uploadFile(
        file: PlatformFile,
        hostProvider: HostProvider,
    ): Flow<UploadResult<String>>

    suspend fun saveFile(file: File)

    suspend fun deleteFile(file: File)
}
