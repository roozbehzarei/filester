package com.roozbehzarei.filester.service

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.roozbehzarei.filester.domain.service.FirebaseService

class FirebaseServiceImpl() : FirebaseService {

    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

    override fun setAnalyticsCollectionEnabled(context: Context, isEnabled: Boolean) {
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(isEnabled)
    }

    override fun setCrashlyticsCollectionEnabled(isEnabled: Boolean) {
        firebaseCrashlytics.isCrashlyticsCollectionEnabled = true
    }

}