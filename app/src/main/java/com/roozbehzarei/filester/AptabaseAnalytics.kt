package com.roozbehzarei.filester

import android.content.Context
import com.aptabase.Aptabase

object AptabaseAnalytics {

    private const val API_KEY = "A-EU-5566501326"

    fun initialize(context: Context) {
        // Initialize Aptabase SDK
        Aptabase.instance.initialize(context, API_KEY)
        // Track app launch
        Aptabase.instance.trackEvent("app_started")
    }

    fun trackUploadSuccess() {
        Aptabase.instance.trackEvent("file_upload_succeeded")
    }

    fun trackUploadFailure() {
        Aptabase.instance.trackEvent("file_upload_failed")
    }

    fun trackUploadCancellation() {
        Aptabase.instance.trackEvent("file_upload_cancelled")
    }

}