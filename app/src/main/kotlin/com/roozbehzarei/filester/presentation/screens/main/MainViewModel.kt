package com.roozbehzarei.filester.presentation.screens.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.presentation.UiText
import com.roozbehzarei.filester.upload.UploadManager
import com.roozbehzarei.filester.upload.UploadState
import com.roozbehzarei.filester.upload.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val fileRepository: FileRepository, private val uploadManager: UploadManager
) : ViewModel() {

    private val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState: StateFlow<MainUiState> = _mainUiState.asStateFlow()

    init {
        getFiles()
        updateUploadStatus()
    }

    private fun updateUploadStatus() {
        viewModelScope.launch {
            uploadManager.status.collect { newStatus ->
                _mainUiState.update {
                    it.copy(uploadStatus = newStatus)
                }
            }
        }
    }

    fun resetUploadStatus() {
        uploadManager.prune()
        _mainUiState.update {
            it.copy(
                uploadStatus = UploadStatus(
                    state = UploadState.INACTIVE, progress = 0
                )
            )
        }
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
        uploadManager.start(uri)
        _mainUiState.update {
            it.copy(uploadingFileName = fileName)
        }
    }

    fun messageShown() {
        _mainUiState.update { it.copy(message = null) }
    }

    fun cancelUpload() {
        uploadManager.cancel()
    }

}