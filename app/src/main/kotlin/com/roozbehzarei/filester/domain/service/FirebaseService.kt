package com.roozbehzarei.filester.domain.service

import android.content.Context

interface FirebaseService {

    fun setAnalyticsCollectionEnabled(context: Context, isEnabled: Boolean)

    fun setCrashlyticsCollectionEnabled(isEnabled: Boolean)

}