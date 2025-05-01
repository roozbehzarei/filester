package com.roozbehzarei.filester.domain.repository

import android.content.Context

interface AptabaseAnalyticsRepository {

    fun initialize(context: Context)

    fun trackUploadSuccess()

    fun trackUploadFailure()

    fun trackUploadCancellation()

}