package com.roozbehzarei.filester.domain.model

sealed interface RemoteResource<out T> {

    data class Success<T>(val data: T, val expiresInHours: Long) : RemoteResource<T>
    data class Loading(val progress: Int) : RemoteResource<Nothing>
    data class Error(val message: String? = null) : RemoteResource<Nothing>

}