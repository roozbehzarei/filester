package com.roozbehzarei.filester.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import androidx.work.*
import com.roozbehzarei.filester.database.File
import com.roozbehzarei.filester.database.FileDao
import com.roozbehzarei.filester.database.MainUiState
import com.roozbehzarei.filester.worker.UploadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val KEY_FILE_URI = "FILE_URI"
const val KEY_FILE_NAME = "FILE_NAME"
const val KEY_WORK = "UNIQUE_WORK"

class FilesterViewModel(private val fileDao: FileDao, application: Application) :
    AndroidViewModel(application) {

    private var inputData: Data? = null
    private val workManager = WorkManager.getInstance(application)
    val outputWorkInfo: LiveData<List<WorkInfo>>
        get() = workManager.getWorkInfosForUniqueWorkLiveData(KEY_WORK)

    val files: LiveData<List<File>> = fileDao.getAll().asLiveData()
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun deleteFile(file: File) {
        viewModelScope.launch {
            try {
                fileDao.delete(file)
                _uiState.update { state ->
                    state.copy(isFileDeleted = true)
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isFileDeleted = false)
                }
            }
        }
    }

    fun uiStateConsumed() {
        _uiState.update { uiState ->
            uiState.copy(isFileDeleted = false)
        }
    }

    fun startUploadWork(uri: Uri, fileName: String) {
        inputData = Data.Builder().putString(KEY_FILE_URI, uri.toString())
            .putString(KEY_FILE_NAME, fileName).build()
        val workRequest =
            OneTimeWorkRequestBuilder<UploadWorker>().setInputData(inputData!!).build()
        workManager.enqueueUniqueWork(
            KEY_WORK, ExistingWorkPolicy.REPLACE, workRequest
        )
    }

    fun clearWorkQueue() {
        workManager.pruneWork()
    }

}

class FilesterViewModelFactory(
    private val fileDao: FileDao, private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilesterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return FilesterViewModel(
                fileDao, application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}