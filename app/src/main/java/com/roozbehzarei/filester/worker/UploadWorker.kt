package com.roozbehzarei.filester.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.roozbehzarei.filester.BaseApplication
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.network.TransferApi
import com.roozbehzarei.filester.showNotification
import com.roozbehzarei.filester.viewmodel.KEY_FILE_NAME
import com.roozbehzarei.filester.viewmodel.KEY_FILE_URI
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadWorker(private val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val resolver = context.contentResolver
    private val fileDao = BaseApplication().database.fileDao()

    override suspend fun doWork(): Result {
        return try {
            showNotification(
                context = applicationContext,
                title = applicationContext.getString(R.string.notification_title_in_progress),
                description = applicationContext.getString(R.string.notification_description_in_progress),
                notificationId = 1,
                showProgress = true
            )
            val resourceUri = Uri.parse(inputData.getString(KEY_FILE_URI))
            val fileName = inputData.getString(KEY_FILE_NAME)
            val mediaType = MediaType.parse(resolver.getType(resourceUri)!!)
            val inputStream = resolver.openInputStream(resourceUri)
            val file = File(context.cacheDir, fileName!!)
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                    output.flush()
                }
            }
            inputStream?.close()
            file.length()
            val filePart = MultipartBody.Part.createFormData(
                "files",
                file.name,
                RequestBody.create(mediaType, file)
            )
            val apiResponse = TransferApi.retrofitService.sendFile(filePart)
            val responseBody = apiResponse.body()
            if (apiResponse.isSuccessful && !responseBody.isNullOrEmpty()) {
                val outputData = workDataOf(KEY_FILE_URI to responseBody)
                val newFileEntry = com.roozbehzarei.filester.database.File(
                    fileName = file.name,
                    fileUrl = responseBody,
                    fileSize = file.length() / 1024 / 1024
                )
                fileDao.insert(newFileEntry)
                showNotification(
                    context = applicationContext,
                    title = applicationContext.getString(R.string.title_upload_success),
                    description = applicationContext.getString(R.string.message_upload_success),
                    notificationId = 1,
                    showProgress = false
                )
                Result.success(outputData)
            } else {
                showNotification(
                    context = applicationContext,
                    title = applicationContext.getString(R.string.title_upload_error),
                    description = applicationContext.getString(R.string.message_upload_error),
                    notificationId = 1,
                    showProgress = false
                )
                Result.failure()
            }
        } catch (e: Exception) {
            showNotification(
                context = applicationContext,
                title = applicationContext.getString(R.string.title_upload_error),
                description = applicationContext.getString(R.string.message_upload_error),
                notificationId = 1,
                showProgress = false
            )
            Result.failure()
        }
    }
}