package com.roozbehzarei.filester.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.roozbehzarei.filester.UploadWorker
import com.roozbehzarei.filester.data.local.File
import com.roozbehzarei.filester.data.local.FileDao
import com.roozbehzarei.filester.data.network.filester.FilesterApi
import com.roozbehzarei.filester.ui.screens.main.MainUiState
import com.roozbehzarei.filester.ui.screens.settings.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

const val KEY_FILE_URI = "FILE_URI"
const val KEY_FILE_NAME = "FILE_NAME"
const val KEY_WORK = "UNIQUE_WORK"

@Single
class SharedViewModel(private val fileDao: FileDao, application: Application) :
    AndroidViewModel(application) {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()
    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()
    private var _shouldShowUploadFab = MutableStateFlow(false)
    val shouldShowUploadFab: StateFlow<Boolean> = _shouldShowUploadFab.asStateFlow()
    private val workManager = WorkManager.getInstance(application)
    private lateinit var inputData: Data
    private val filesterApi = FilesterApi.retrofitService

    init {
        viewModelScope.launch {
            fileDao.getAll().collect { filesList ->
                _mainUiState.update { it.copy(files = filesList) }
            }
        }
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(KEY_WORK).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    _mainUiState.update {
                        it.copy(uploadStatus = workInfos.first().state)
                    }
                }
            }
        }
        getAppVersion()
    }

    fun setUploadFabVisibility(isVisible: Boolean) {
        _shouldShowUploadFab.value = isVisible
    }

    fun deleteFile(file: File) {
        viewModelScope.launch {
            try {
                fileDao.delete(file)
                _mainUiState.update { state ->
                    state.copy(isFileDeleted = true)
                }
            } catch (_: Exception) {
                _mainUiState.update { state ->
                    state.copy(isFileDeleted = false)
                }
            }
        }
    }

    fun initializeUpload(uri: Uri, fileName: String) {
        inputData = Data.Builder().putString(KEY_FILE_URI, uri.toString())
            .putString(KEY_FILE_NAME, fileName).build()
        val workRequest =
            OneTimeWorkRequestBuilder<UploadWorker>().setInputData(inputData).build()
        workManager.enqueueUniqueWork(
            KEY_WORK, ExistingWorkPolicy.REPLACE, workRequest
        )
        _mainUiState.update {
            it.copy(uploadingFileName = fileName)
        }
    }

    fun cancelUpload() {
        workManager.cancelAllWork()
    }

    private fun getAppVersion() {
        viewModelScope.launch {
            try {
                val response = filesterApi.getVersion()
                if (response.isSuccessful) {
                    _settingsUiState.update { it.copy(appConfig = response.body()) }
                }
            } catch (_: Exception) {
            }
        }
    }

}