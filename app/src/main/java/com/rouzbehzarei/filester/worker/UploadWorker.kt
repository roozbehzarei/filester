package com.rouzbehzarei.filester.worker

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rouzbehzarei.filester.BaseApplication
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.network.TransferApi
import com.rouzbehzarei.filester.showNotification
import com.rouzbehzarei.filester.viewmodel.KEY_FILE_URI
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadWorker(context: Context, params: WorkerParameters) :
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
            val resourceUri = (Uri.parse(inputData.getString(KEY_FILE_URI)))
            val mediaType = MediaType.parse(resolver.getType(resourceUri)!!)
            val extension =
                "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mediaType.toString())
            val inputStream = resolver.openInputStream(resourceUri)
            val file = File.createTempFile("filester-", extension)
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
                val newFileEntry = com.rouzbehzarei.filester.database.File(
                    fileName = file.name,
                    fileUrl = responseBody,
                    fileSize = file.length() / 1024 / 1024
                )
                fileDao.insert(newFileEntry)
                showNotification(
                    context = applicationContext,
                    title = applicationContext.getString(R.string.notification_title_successful),
                    description = applicationContext.getString(R.string.notification_description_successful),
                    notificationId = 1,
                    showProgress = false
                )
                Result.success(outputData)
            } else {
                showNotification(
                    context = applicationContext,
                    title = applicationContext.getString(R.string.notification_title_unsuccessful),
                    description = applicationContext.getString(R.string.notification_description_unsuccessful),
                    notificationId = 1,
                    showProgress = false
                )
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}