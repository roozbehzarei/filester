package com.roozbehzarei.filester.upload

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.data.network.catbox.CatboxResult
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.domain.service.FirebaseService
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class UploadWorker(
    private val context: Context,
    private val notificationHelper: UploadNotificationHelper,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val fileRepository: FileRepository by inject()
    private val firebaseService: FirebaseService by inject()

    override suspend fun doWork(): Result {

        setForeground(notificationHelper.createForegroundInfo())

        val inputFileUri = inputData.getString(KEY_FILE_URI)?.toUri() ?: return Result.failure()
        val inputFile = DocumentFile.fromSingleUri(context, inputFileUri)
        val fileToUpload = java.io.File(context.cacheDir, inputFile?.name.orEmpty())
        try {
            context.contentResolver.openInputStream(inputFileUri)?.use { inputStream ->
                FileOutputStream(fileToUpload).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            val result = fileRepository.uploadFile(fileToUpload).onEach { result ->
                if (result is CatboxResult.Loading) {
                    notificationHelper.updateOngoingNotification(
                        R.string.notif_title_in_progress, result.progress
                    )
                    setProgress(workDataOf(KEY_WORK_PROGRESS to result.progress))
                }
            }.first { it !is CatboxResult.Loading }

            fileToUpload.delete()

            return when (result) {
                is CatboxResult.Error -> {
                    notificationHelper.postResultNotification(R.string.title_upload_error)
                    firebaseService.logUploadFailure()
                    Result.failure()
                }

                is CatboxResult.Success -> {
                    val uploadedTime = System.currentTimeMillis()
                    val expirationTime = uploadedTime + TimeUnit.HOURS.toMillis(72)
                    val uploadedFile = File(
                        name = inputFile?.name.orEmpty(),
                        downloadUrl = result.url,
                        size = inputFile?.length() ?: 0,
                        mimeType = inputFile?.type,
                        uploadedAt = uploadedTime,
                        expiresAt = expirationTime
                    )
                    fileRepository.saveFile(uploadedFile)
                    notificationHelper.postResultNotification(R.string.notif_title_upload_success)
                    firebaseService.logUploadSuccess()
                    Result.success()
                }

                else -> {
                    firebaseService.logUploadFailure()
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            fileToUpload.delete()
            notificationHelper.cancelOngoingNotification()
            if (e is CancellationException) {
                notificationHelper.postResultNotification(R.string.notif_title_upload_cancelled)
            } else {
                firebaseService.logUploadFailure()
            }
            if (BuildConfig.DEBUG) e.printStackTrace()
            return Result.failure()
        }
    }

    companion object {
        const val KEY_FILE_URI = "file_uri"
        const val KEY_WORK_NAME = "upload_work_name"
        const val KEY_WORK_PROGRESS = "upload_work_progress"

    }

}