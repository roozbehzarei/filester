package com.roozbehzarei.filester.data.repository

import android.content.Context
import com.aptabase.Aptabase
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.domain.repository.AptabaseAnalyticsRepository

object AptabaseAnalyticsRepositoryImpl : AptabaseAnalyticsRepository {

    private val apiKey = BuildConfig.APTABASE_API_KEY

    override fun initialize(context: Context) {
        // Initialize Aptabase SDK
        Aptabase.Companion.instance.initialize(context, apiKey)
        // Track app launch
        Aptabase.Companion.instance.trackEvent("app_started")
    }

    override fun trackUploadSuccess() {
        Aptabase.Companion.instance.trackEvent("file_upload_succeeded")
    }

    override fun trackUploadFailure() {
        Aptabase.Companion.instance.trackEvent("file_upload_failed")
    }

    override fun trackUploadCancellation() {
        Aptabase.Companion.instance.trackEvent("file_upload_cancelled")
    }

}