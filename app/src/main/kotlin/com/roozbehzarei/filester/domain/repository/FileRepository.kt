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
