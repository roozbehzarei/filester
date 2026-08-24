package com.roozbehzarei.filester.upload

import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.UploadResult
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.domain.service.AnalyticsService
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.releaseBookmark
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class UploadWorker(
    context: Context,
    private val notificationFactory: UploadNotificationFactory,
    private val fileRepository: FileRepository,
    private val analyticsService: AnalyticsService,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    val ongoingNotificationId = id.hashCode()
    val resultNotificationId = ongoingNotificationId + 1

    override suspend fun doWork(): Result {
        setForeground(
            createForegroundInfo(
                title = applicationContext.getString(R.string.notif_title_start),
                text = "",
                progress = 0,
            ),
        )

        val inputFile = PlatformFile(inputData.getString(KEY_FILE_PATH) ?: return Result.failure())
        try {
            val hostProvider =
                HostProvider.fromId(inputData.getString(KEY_HOST_PROVIDER))
                    ?: return Result.failure()
            val fileName =
                inputFile.name.takeIf { it.isNotBlank() } ?: "file_${System.currentTimeMillis()}"
            val fileSize = inputFile.size().takeIf { it >= 0 } ?: return Result.failure()
            val fileType = inputFile.mimeType()?.toString()
            val result =
                fileRepository
                    .uploadFile(inputFile, hostProvider)
                    .onEach { result ->
                        if (result is UploadResult.Loading) {
                            setForeground(
                                createForegroundInfo(
                                    title = applicationContext.getString(R.string.notif_title_in_progress),
                                    text = "",
                                    progress = result.progress,
                                ),
                            )
                            setProgress(workDataOf(KEY_WORK_PROGRESS to result.progress))
                        }
                    }.first { it !is UploadResult.Loading }

            return when (result) {
                is UploadResult.Error -> {
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_failed),
                        text = "",
                    )
                    analyticsService.logUploadFailure()
                    Result.failure()
                }

                is UploadResult.Success -> {
                    val uploadedFile =
                        File(
                            name = fileName,
                            downloadUrl = result.data,
                            size = fileSize,
                            mimeType = fileType,
                            uploadedAt = System.currentTimeMillis(),
                            expiresAt = result.expiresAt,
                        )
                    fileRepository.saveFile(uploadedFile)
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_success),
                        text = "",
                    )
                    analyticsService.logUploadSuccess()
                    Result.success()
                }

                else -> {
                    analyticsService.logUploadFailure()
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            withContext(NonCancellable) {
                if (e is CancellationException) {
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_cancelled),
                        text = "",
                    )
                    delay(200.milliseconds)
                    throw e
                } else {
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_failed),
                        text = "",
                    )
                    analyticsService.logUploadFailure()
                    delay(200.milliseconds)
                }
            }
            if (BuildConfig.DEBUG) e.printStackTrace()
            return Result.failure()
        } finally {
            runCatching { inputFile.releaseBookmark() }
        }
    }

    private fun createForegroundInfo(
        title: String,
        text: String,
        progress: Int,
    ): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                ongoingNotificationId,
                notificationFactory.createOngoing(
                    title = title,
                    text = text,
                    progress = progress,
                    cancelIntent = intent,
                ),
                FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            ForegroundInfo(
                ongoingNotificationId,
                notificationFactory.createOngoing(
                    title = title,
                    text = text,
                    progress = progress,
                    cancelIntent = intent,
                ),
            )
        }
    }

    companion object {
        const val KEY_FILE_PATH = "file_path"
        const val KEY_HOST_PROVIDER = "host_provider"
        const val KEY_WORK_NAME = "upload_work_name"
        const val KEY_WORK_PROGRESS = "upload_work_progress"
    }
}
