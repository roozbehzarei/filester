package com.roozbehzarei.filester.domain.repository

import android.content.Context

interface AptabaseAnalyticsRepository {

    fun initialize(context: Context)

    fun setupKey(key: String)

    fun trackUploadSuccess()

    fun trackUploadFailure()

    fun trackUploadCancellation()

}