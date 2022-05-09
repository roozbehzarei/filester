package com.rouzbehzarei.filester.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rouzbehzarei.filester.network.TransferApi
import com.rouzbehzarei.filester.viewmodel.KEY_FILE_URI
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UploadWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val resolver = context.contentResolver

    override suspend fun doWork(): Result {
        return try {
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
            val filePart = MultipartBody.Part.createFormData(
                "files",
                file.name,
                RequestBody.create(mediaType, file)
            )
            val apiResponse = TransferApi.retrofitService.sendFile(filePart)
            val responseBody = apiResponse.body()
            if(apiResponse.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val outputData = workDataOf(KEY_FILE_URI to responseBody)
                    Log.d("SendFileWorker", responseBody)
                    Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.d("SendFileWorker", e.toString())
            Result.failure()
        }
    }
}