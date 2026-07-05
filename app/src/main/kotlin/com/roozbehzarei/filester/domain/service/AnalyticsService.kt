package com.roozbehzarei.filester.domain.service


interface AnalyticsService {

    fun setEnabled(enable: Boolean)

    fun logUploadSuccess()

    fun logUploadFailure()

}