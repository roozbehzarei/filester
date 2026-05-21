package com.roozbehzarei.filester.presentation.screens.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.framework.UploadWorker
import com.roozbehzarei.filester.presentation.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val KEY_FILE_URI = "file_uri"
const val KEY_WORK_NAME = "upload_work_name"
const val KEY_WORK_PROGRESS = "upload_work_progress"

class MainViewModel(
    private val fileRepository: FileRepository, private val workManager: WorkManager
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()
    private lateinit var inputData: Data

    init {
        getFiles()
        updateUploadStatus()
    }

    private fun updateUploadStatus() {
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow(KEY_WORK_NAME).collect { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val latestWork = workInfos.first()

                    _mainUiState.update { state ->
                        state.copy(
                            uploadStatus = latestWork.state,
                            uploadProgress = latestWork.progress.getInt(KEY_WORK_PROGRESS, 0),
                            message = when (latestWork.state) {
                                WorkInfo.State.SUCCEEDED -> UiText.StringResource(R.string.main_text_upload_success)
                                WorkInfo.State.CANCELLED -> UiText.StringResource(R.string.main_text_upload_cancelled)
                                else -> state.message
                            }
                        )
                    }
                    if (latestWork.state == WorkInfo.State.SUCCEEDED || latestWork.state == WorkInfo.State.CANCELLED) {
                        resetUploadStatus()
                    }
                }
            }
        }
    }

    fun resetUploadStatus() {
        workManager.pruneWork()
        _mainUiState.update { it.copy(uploadStatus = null, uploadProgress = null) }
    }

    private fun getFiles() {
        viewModelScope.launch {
            fileRepository.getFiles().collect { filesList ->
                _mainUiState.update { it.copy(files = filesList) }
            }
        }
    }

    fun deleteFile(file: File) {
        viewModelScope.launch {
            try {
                fileRepository.deleteFile(file)
                _mainUiState.update { it.copy(message = UiText.StringResource(R.string.main_snackbar_delete_successful)) }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                // TODO: Show deletion failure message
            }
        }
    }

    fun initializeUpload(uri: Uri, fileName: String) {
        inputData = Data.Builder().putString(KEY_FILE_URI, uri.toString()).build()
        val workRequest = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(inputData).build()
        workManager.enqueueUniqueWork(
            KEY_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest
        )
        _mainUiState.update {
            it.copy(uploadingFileName = fileName)
        }
    }

    fun messageShown() {
        _mainUiState.update { it.copy(message = null) }
    }

    fun cancelUpload() {
        workManager.cancelAllWork()
    }

}