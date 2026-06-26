package com.roozbehzarei.filester.service

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.roozbehzarei.filester.domain.service.FirebaseService

class FirebaseServiceImpl(context: Context) : FirebaseService {

    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun setAnalyticsCollectionEnabled(isEnabled: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled)
    }

    override fun setCrashlyticsCollectionEnabled(isEnabled: Boolean) {
        firebaseCrashlytics.isCrashlyticsCollectionEnabled = isEnabled
    }

    override fun logUploadSuccess() {
        firebaseAnalytics.logEvent("file_upload_success", null)
    }

    override fun logUploadFailure() {
        firebaseAnalytics.logEvent("file_upload_failure", null)
    }

}