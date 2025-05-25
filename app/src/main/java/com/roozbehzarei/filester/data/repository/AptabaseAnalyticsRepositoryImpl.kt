package com.roozbehzarei.filester.data.repository

import android.content.Context
import com.aptabase.Aptabase
import com.roozbehzarei.filester.domain.repository.AptabaseAnalyticsRepository
import org.koin.core.annotation.Single

@Single
class AptabaseAnalyticsRepositoryImpl() : AptabaseAnalyticsRepository {

    private var apiKey = ""

    override fun initialize(context: Context) {
        if (apiKey.isNotBlank()) {
            // Initialize Aptabase SDK
            Aptabase.instance.initialize(context, apiKey)
            // Track app launch
            Aptabase.instance.trackEvent("app_started")
        }
    }

    override fun setupKey(key: String) {
        apiKey = key
    }

    override fun trackUploadSuccess() {
        if (apiKey.isNotBlank())
            Aptabase.instance.trackEvent("file_upload_succeeded")
    }

    override fun trackUploadFailure() {
        if (apiKey.isNotBlank())
            Aptabase.instance.trackEvent("file_upload_failed")
    }

    override fun trackUploadCancellation() {
        if (apiKey.isNotBlank())
            Aptabase.instance.trackEvent("file_upload_cancelled")
    }

}