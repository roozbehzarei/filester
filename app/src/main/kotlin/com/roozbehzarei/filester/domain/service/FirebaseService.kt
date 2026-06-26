package com.roozbehzarei.filester.domain.service


interface FirebaseService {

    fun setAnalyticsCollectionEnabled(isEnabled: Boolean)

    fun setCrashlyticsCollectionEnabled(isEnabled: Boolean)

    fun logUploadSuccess()

    fun logUploadFailure()

}