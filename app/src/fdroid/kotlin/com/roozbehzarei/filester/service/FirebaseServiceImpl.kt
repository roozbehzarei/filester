package com.roozbehzarei.filester.service

import android.content.Context
import com.roozbehzarei.filester.domain.service.FirebaseService

class FirebaseServiceImpl : FirebaseService {

    override fun setAnalyticsCollectionEnabled(isEnabled: Boolean) {}

    override fun setPerformanceMonitoringEnabled(isEnabled: Boolean) {}

    override fun setCrashlyticsCollectionEnabled(isEnabled: Boolean) {}

    override fun logUploadSuccess() {}

    override fun logUploadFailure() {}

}