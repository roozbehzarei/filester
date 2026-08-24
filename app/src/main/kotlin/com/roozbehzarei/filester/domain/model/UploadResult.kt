package com.roozbehzarei.filester.domain.model

sealed interface UploadResult<out T> {
    /**
     * @param expiresAt when the uploaded file is deleted by the host, as epoch milliseconds.
     * Each host decides this per upload: a fixed retention for some, a function of file size for
     * others.
     */
    data class Success<T>(
        val data: T,
        val expiresAt: Long,
    ) : UploadResult<T>

    data class Loading(
        val progress: Int,
    ) : UploadResult<Nothing>

    data class Error(
        val message: String? = null,
    ) : UploadResult<Nothing>
}
