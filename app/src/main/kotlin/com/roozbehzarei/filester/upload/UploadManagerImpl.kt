package com.roozbehzarei.filester.upload

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_FILE_PATH
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_HOST_PROVIDER
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_WORK_NAME
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_WORK_PROGRESS
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.bookmarkData
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UploadManagerImpl(
    private val workManager: WorkManager,
    private val userPreferencesRepository: UserPreferencesRepository,
) : UploadManager {
    override val status: Flow<UploadStatus> =
        workManager.getWorkInfosForUniqueWorkFlow(KEY_WORK_NAME).map { workInfos ->
            val workInfo = workInfos.firstOrNull()
            if (workInfo != null) {
                val state =
                    when (workInfo.state) {
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

    override suspend fun start(file: PlatformFile) {
        runCatching { file.bookmarkData() }

        val hostProvider = userPreferencesRepository.getHostProviderPreference().first()
        val inputData =
            workDataOf(
                KEY_FILE_PATH to file.path,
                KEY_HOST_PROVIDER to hostProvider.id,
            )
        val workRequest = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(inputData).build()
        workManager.enqueueUniqueWork(
            KEY_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    override fun cancel() {
        workManager.cancelUniqueWork(KEY_WORK_NAME)
    }

    override fun prune() {
        workManager.pruneWork()
    }
}
