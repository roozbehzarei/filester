package com.rouzbehzarei.filester.model

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rouzbehzarei.filester.network.TransferApi
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

enum class TransferApiStatus {
    LOADING, ERROR, DONE
}

class UploadViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _responseStatus = MutableLiveData<TransferApiStatus?>()

    // The external immutable LiveData for the request status
    val responseStatus: LiveData<TransferApiStatus?> = _responseStatus

    // The internal MutableLiveData that stores the server response
    private var _apiResponse = MutableLiveData<Response<String>>()

    // The external immutable LiveData for the server response
    val apiResponse: LiveData<Response<String>> = _apiResponse

    /**
     * Create a temporary file of a given Uri with the same extension
     */
    fun prepareForUpload(uri: Uri, contentResolver: ContentResolver) {
        val mediaType = MediaType.parse(contentResolver.getType(uri)!!)
        val extension =
            "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(mediaType.toString())
        val inputStream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("filester-", extension)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        val filePart = MultipartBody.Part.createFormData(
            "files",
            file.name,
            RequestBody.create(mediaType, file)
        )
        uploadToServer(filePart)
    }

    /**
     * Post the user's selected file to the webserver
     */
    private fun uploadToServer(filePart: MultipartBody.Part) {
        _responseStatus.value = TransferApiStatus.LOADING
        viewModelScope.launch {
            try {
                _apiResponse.value = TransferApi.retrofitService.sendFile(filePart)
                if (_apiResponse.value!!.isSuccessful) {
                    _responseStatus.value = TransferApiStatus.DONE
                } else
                    _responseStatus.value = TransferApiStatus.ERROR
            } catch (e: Exception) {
                _responseStatus.value = TransferApiStatus.ERROR
            }
        }
    }

    fun resetStatus() {
        _responseStatus.value = null
    }

}