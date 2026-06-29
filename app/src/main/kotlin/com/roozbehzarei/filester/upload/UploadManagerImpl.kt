package com.roozbehzarei.filester.upload

import android.net.Uri
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_FILE_URI
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_WORK_NAME
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_WORK_PROGRESS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UploadManagerImpl(private val workManager: WorkManager) : UploadManager {

    override val status: Flow<UploadStatus> =
        workManager.getWorkInfosForUniqueWorkFlow(KEY_WORK_NAME).map { workInfos ->
            val workInfo = workInfos.firstOrNull()
            if (workInfo != null) {
                val state = when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> UploadState.SUCCEEDED
                    WorkInfo.State.FAILED -> UploadState.FAILED
                    WorkInfo.State.CANCELLED -> UploadState.CANCELLED
                    WorkInfo.State.RUNNING -> UploadState.RUNNING
                    else -> UploadState.INACTIVE
                }
                val progress = workInfo.progress.getInt(KEY_WORK_PROGRESS, 0)
                UploadStatus(state, progress)
            } else {
                UploadStatus(UploadState.INACTIVE, 0)
            }
        }

    override fun start(uri: Uri) {
        val inputData = Data.Builder().putString(KEY_FILE_URI, uri.toString()).build()
        val workRequest = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(inputData).build()
        workManager.enqueueUniqueWork(
            KEY_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest
        )
    }

    override fun cancel() {
        workManager.cancelUniqueWork(KEY_WORK_NAME)
    }

    override fun prune() {
        workManager.pruneWork()
    }

}