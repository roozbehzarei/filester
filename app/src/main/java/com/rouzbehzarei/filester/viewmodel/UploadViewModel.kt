package com.rouzbehzarei.filester.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.rouzbehzarei.filester.worker.UploadWorker

const val KEY_FILE_URI = "FILE_URI"
private const val KEY_WORK = "UNIQUE_WORK"

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    private var inputData: Data? = null
    private val workManager = WorkManager.getInstance(application)
    val outputWorkInfo: LiveData<List<WorkInfo>>
        get() = workManager.getWorkInfosForUniqueWorkLiveData(KEY_WORK)

    fun startUniqueWork(uri: Uri) {
        inputData = Data.Builder()
            .putString(KEY_FILE_URI, uri.toString())
            .build()
        val workRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(inputData!!)
            .build()
        workManager.enqueueUniqueWork(
            KEY_WORK,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun clearWorkQueue() {
        workManager.pruneWork()
    }

}