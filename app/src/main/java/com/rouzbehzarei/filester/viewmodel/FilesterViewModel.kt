package com.rouzbehzarei.filester.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import com.rouzbehzarei.filester.database.File
import com.rouzbehzarei.filester.database.FileDao
import com.rouzbehzarei.filester.worker.UploadWorker

const val KEY_FILE_URI = "FILE_URI"
private const val KEY_WORK = "UNIQUE_WORK"

class FilesterViewModel(fileDao: FileDao, application: Application) :
    AndroidViewModel(application) {

    private var inputData: Data? = null
    private val workManager = WorkManager.getInstance(application)
    val outputWorkInfo: LiveData<List<WorkInfo>>
        get() = workManager.getWorkInfosForUniqueWorkLiveData(KEY_WORK)

    val files: LiveData<List<File>> = fileDao.getAll().asLiveData()

    fun startUploadWork(uri: Uri) {
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

class FilesterViewModelFactory(
    private val fileDao: FileDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilesterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilesterViewModel(
                fileDao, application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}