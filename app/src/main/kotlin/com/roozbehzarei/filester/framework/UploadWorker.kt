package com.roozbehzarei.filester.framework

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.data.network.catbox.CatboxResult
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.presentation.screens.main.KEY_FILE_URI
import com.roozbehzarei.filester.presentation.screens.main.KEY_WORK_PROGRESS
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.FileOutputStream

private const val ONGOING_NOTIFICATION_ID = 20
private const val FINAL_NOTIFICATION_ID = 10

class UploadWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val fileRepository: FileRepository by inject()
    private lateinit var notificationManager: NotificationManager
    private lateinit var ongoingNotificationBuilder: NotificationCompat.Builder
    private lateinit var fileToUpload: java.io.File

    override suspend fun doWork(): Result {

        setForeground(createForegroundInfo())
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val inputFileUri = inputData.getString(KEY_FILE_URI)?.toUri() ?: return Result.failure()
        val inputFile = DocumentFile.fromSingleUri(context, inputFileUri)
        fileToUpload = java.io.File(context.cacheDir, inputFile?.name.orEmpty())
        try {
            context.contentResolver.openInputStream(inputFileUri)?.use { inputStream ->
                FileOutputStream(fileToUpload).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            val result = fileRepository.uploadFile(fileToUpload).onEach { result ->
                if (result is CatboxResult.Loading) {
                    updateOngoingNotification(
                        R.string.notif_title_in_progress, result.progress
                    )
                    setProgress(workDataOf(KEY_WORK_PROGRESS to result.progress))
                }
            }.filter { it !is CatboxResult.Loading }.first()

            fileToUpload.delete()

            return when (result) {
                is CatboxResult.Error -> {
                    postResultNotification(R.string.title_upload_error)
                    Result.failure()
                }

                is CatboxResult.Success -> {
                    val uploadedFile = File(
                        name = inputFile?.name.orEmpty(),
                        downloadUrl = result.url,
                        size = inputFile?.length() ?: 0,
                        mimeType = inputFile?.type
                    )
                    fileRepository.saveFile(uploadedFile)
                    postResultNotification(R.string.notif_title_upload_success)
                    Result.success()
                }

                else -> Result.failure()
            }
        } catch (e: Exception) {
            fileToUpload.delete()
            notificationManager.cancel(ONGOING_NOTIFICATION_ID)
            if (e is CancellationException) {
                postResultNotification(R.string.notif_title_upload_cancelled)
            }
            if (BuildConfig.DEBUG) e.printStackTrace()
            return Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val channelId = context.getString(R.string.notification_channel_id)
        val cancelIntent = Intent(context, FilesterBroadcastReceiver::class.java).apply {
            this.action = "FILESTER_STOP_UPLOAD"
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, -1, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )
        ongoingNotificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.notif_title_start))
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(applicationContext, R.color.seed)).setOngoing(true)
            .addAction(0, context.getString(R.string.cancel), cancelPendingIntent)
            .setProgress(100, 0, false)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                ONGOING_NOTIFICATION_ID,
                ongoingNotificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(ONGOING_NOTIFICATION_ID, ongoingNotificationBuilder.build())
        }
    }

    private fun updateOngoingNotification(@StringRes titleResource: Int, currentProgress: Int) {
        ongoingNotificationBuilder.setContentTitle(context.getString(titleResource))
            .setProgress(100, currentProgress, false)
        notificationManager.notify(ONGOING_NOTIFICATION_ID, ongoingNotificationBuilder.build())
    }

    private fun postResultNotification(@StringRes titleResource: Int) {
        val channelId = context.getString(R.string.notification_channel_id)
        val resultNotification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(titleResource))
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(applicationContext, R.color.seed)).setOngoing(false)
            .build()
        notificationManager.notify(FINAL_NOTIFICATION_ID, resultNotification)
    }

}