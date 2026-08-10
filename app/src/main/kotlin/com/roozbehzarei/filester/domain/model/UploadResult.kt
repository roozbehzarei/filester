package com.roozbehzarei.filester.domain.model

sealed interface UploadResult<out T> {
    data class Success<T>(
        val data: T,
        val expiresInHours: Long,
    ) : UploadResult<T>

    data class Loading(
        val progress: Int,
    ) : UploadResult<Nothing>

    data class Error(
        val message: String? = null,
    ) : UploadResult<Nothing>
}
