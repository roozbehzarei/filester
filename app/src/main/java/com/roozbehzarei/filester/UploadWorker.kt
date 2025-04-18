package com.roozbehzarei.filester

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.roozbehzarei.filester.data.local.FileDao
import com.roozbehzarei.filester.data.network.oshi.OshiApi
import com.roozbehzarei.filester.domain.ParseOshiResponseUseCase
import com.roozbehzarei.filester.ui.KEY_FILE_NAME
import com.roozbehzarei.filester.ui.KEY_FILE_URI
import com.roozbehzarei.filester.ui.MainActivity
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class UploadWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val resolver = context.contentResolver
    private val fileDao: FileDao by inject()

    override suspend fun doWork(): Result {

        setForeground(
            createForegroundInfo(
                context.getString(R.string.notification_title_start)
            )
        )

        val resourceUri = inputData.getString(KEY_FILE_URI)!!.toUri()
        val fileName = inputData.getString(KEY_FILE_NAME)
        val mediaType = MediaType.parse(resolver.getType(resourceUri)!!)
        val inputStream = resolver.openInputStream(resourceUri)
        val file = File(context.cacheDir, fileName!!)

        return try {
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                    output.flush()
                }
            }
            inputStream?.close()
            file.length()
            val filePart = MultipartBody.Part.createFormData(
                "files", file.name, RequestBody.create(mediaType, file)
            )
            setForeground(
                createForegroundInfo(
                    context.getString(R.string.notification_title_in_progress)
                )
            )
            val apiResponse = OshiApi.retrofitService.sendFile(filePart)
            if (apiResponse.isSuccessful && !apiResponse.body().isNullOrEmpty()) {
                val oshiResponse = ParseOshiResponseUseCase().invoke(apiResponse.body()!!)
                val outputData = workDataOf(KEY_FILE_URI to oshiResponse.downloadUrl)
                val newFileEntry = com.roozbehzarei.filester.data.local.File(
                    name = file.name,
                    url = oshiResponse.downloadUrl,
                    size = file.length(),
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
                )
                fileDao.insert(newFileEntry)
                notificationManager.notify(
                    1, postNotification(context.getString(R.string.title_upload_success))
                )
                Result.success(outputData)
            } else {
                notificationManager.notify(
                    1, postNotification(context.getString(R.string.title_upload_error))
                )
                Result.failure()
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                notificationManager.notify(
                    0, postNotification(context.getString(R.string.title_upload_cancelled))
                )
            } else {
                notificationManager.notify(
                    0, postNotification(context.getString(R.string.title_upload_error))
                )
            }
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val cancelIntent = Intent(context, FilesterBroadcastReceiver::class.java).apply {
            this.setAction("FILESTER_STOP_UPLOAD")
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, -1, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notificationChannelId = context.getString(R.string.notification_channel_id)
        val notification =
            NotificationCompat.Builder(context, notificationChannelId).setContentTitle(progress)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(applicationContext, R.color.seed)).setOngoing(true)
                .addAction(0, context.getString(R.string.button_cancel), cancelPendingIntent)
                .setProgress(0, 0, true).build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(0, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(0, notification)
        }
    }

    private fun postNotification(progress: String): Notification {
        val notificationChannelId = context.getString(R.string.notification_channel_id)
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(applicationContext, notificationChannelId)
            .setContentTitle(progress).setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(applicationContext, R.color.seed)).setAutoCancel(true)
            .setContentIntent(pendingIntent).build()
    }
}