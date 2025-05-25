package com.roozbehzarei.filester.data.repository

import android.content.Context
import com.aptabase.Aptabase
import com.roozbehzarei.filester.domain.repository.AptabaseAnalyticsRepository
import org.koin.core.annotation.Single

@Single
class AptabaseAnalyticsRepositoryImpl() : AptabaseAnalyticsRepository {

    private val apiKey = "A-EU-5566501326"

    override fun initialize(context: Context) {
        // Initialize Aptabase SDK
        Aptabase.instance.initialize(context, apiKey)
        // Track app launch
        Aptabase.instance.trackEvent("app_started")
    }

    override fun trackUploadSuccess() {
        Aptabase.instance.trackEvent("file_upload_succeeded")
    }

    override fun trackUploadFailure() {
        Aptabase.instance.trackEvent("file_upload_failed")
    }

    override fun trackUploadCancellation() {
        Aptabase.instance.trackEvent("file_upload_cancelled")
    }

}