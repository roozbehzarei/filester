package com.roozbehzarei.filester.upload

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

interface UploadManager {
    val status: Flow<UploadStatus>

    suspend fun start(file: PlatformFile)

    fun cancel()

    fun prune()
}
