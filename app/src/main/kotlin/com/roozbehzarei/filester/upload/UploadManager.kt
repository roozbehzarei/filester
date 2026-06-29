package com.roozbehzarei.filester.upload

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface UploadManager {

    val status : Flow<UploadStatus>
    fun start(uri: Uri)
    fun cancel()
    fun prune()
}