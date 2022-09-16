package com.roozbehzarei.filester.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import com.roozbehzarei.filester.database.File
import com.roozbehzarei.filester.database.FileDao
import com.roozbehzarei.filester.worker.UploadWorker

const val KEY_FILE_URI = "FILE_URI"
const val KEY_FILE_NAME = "FILE_NAME"
private const val KEY_WORK = "UNIQUE_WORK"

class FilesterViewModel(fileDao: FileDao, application: Application) :
    AndroidViewModel(application) {

    private var inputData: Data? = null
    private val workManager = WorkManager.getInstance(application)
    val outputWorkInfo: LiveData<List<WorkInfo>>
        get() = workManager.getWorkInfosForUniqueWorkLiveData(KEY_WORK)

    val files: LiveData<List<File>> = fileDao.getAll().asLiveData()

    fun startUploadWork(uri: Uri, fileName: String) {
        inputData = Data.Builder()
            .putString(KEY_FILE_URI, uri.toString())
            .putString(KEY_FILE_NAME, fileName)
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