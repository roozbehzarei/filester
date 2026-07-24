package com.roozbehzarei.filester.upload

import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.model.RemoteResource
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.domain.service.AnalyticsService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import okio.FileNotFoundException
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class UploadWorker(
    private val context: Context,
    private val notificationFactory: UploadNotificationFactory,
    private val fileRepository: FileRepository,
    private val analyticsService: AnalyticsService,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    val ongoingNotificationId = id.hashCode()
    val resultNotificationId = ongoingNotificationId + 1

    override suspend fun doWork(): Result {

        setForeground(
            createForegroundInfo(
                title = applicationContext.getString(R.string.notif_title_start),
                text = "",
                progress = 0
            )
        )

        val inputFileUri = inputData.getString(KEY_FILE_URI)?.toUri() ?: return Result.failure()
        val inputFile = DocumentFile.fromSingleUri(context, inputFileUri) ?: return Result.failure()
        val fileName =
            inputFile.name?.takeIf { it.isNotBlank() } ?: "file_${System.currentTimeMillis()}"
        val fileSize = inputFile.length()
        val fileType = inputFile.type
        val fileToUpload = java.io.File(context.cacheDir, fileName)
        try {
            withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(inputFileUri)
                    ?: throw FileNotFoundException()
                inputStream.use {
                    FileOutputStream(fileToUpload).use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead = inputStream.read(buffer)
                        while (bytesRead >= 0) {
                            ensureActive()
                            outputStream.write(buffer, 0, bytesRead)
                            bytesRead = inputStream.read(buffer)
                        }
                    }
                }
            }
            val result = fileRepository.uploadFile(fileToUpload).onEach { result ->
                if (result is RemoteResource.Loading) {
                    setForeground(
                        createForegroundInfo(
                            title = applicationContext.getString(R.string.notif_title_in_progress),
                            text = "",
                            progress = result.progress
                        )
                    )
                    setProgress(workDataOf(KEY_WORK_PROGRESS to result.progress))
                }
            }.first { it !is RemoteResource.Loading }

            fileToUpload.delete()

            return when (result) {
                is RemoteResource.Error -> {
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_failed),
                        text = ""
                    )
                    analyticsService.logUploadFailure()
                    Result.failure()
                }

                is RemoteResource.Success -> {
                    val uploadedTime = System.currentTimeMillis()
                    val expirationTime = uploadedTime + TimeUnit.HOURS.toMillis(72)
                    val uploadedFile = File(
                        name = fileName,
                        downloadUrl = result.data,
                        size = fileSize,
                        mimeType = fileType,
                        uploadedAt = uploadedTime,
                        expiresAt = expirationTime
                    )
                    fileRepository.saveFile(uploadedFile)
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_success),
                        text = ""
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
                fileToUpload.delete()
                if (e is CancellationException) {
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_cancelled),
                        text = ""
                    )
                    delay(200.milliseconds)
                    throw e
                } else {
                    notificationFactory.createResultAndNotify(
                        id = resultNotificationId,
                        title = applicationContext.getString(R.string.notif_title_upload_failed),
                        text = ""
                    )
                    analyticsService.logUploadFailure()
                    delay(200.milliseconds)
                }
            }
            if (BuildConfig.DEBUG) e.printStackTrace()
            return Result.failure()
        }
    }

    private fun createForegroundInfo(title: String, text: String, progress: Int): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                ongoingNotificationId, notificationFactory.createOngoing(
                    title = title, text = text, progress = progress, cancelIntent = intent
                ), FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                ongoingNotificationId, notificationFactory.createOngoing(
                    title = title, text = text, progress = progress, cancelIntent = intent
                )
            )
        }
    }

    companion object {
        const val KEY_FILE_URI = "file_uri"
        const val KEY_WORK_NAME = "upload_work_name"
        const val KEY_WORK_PROGRESS = "upload_work_progress"

    }

}