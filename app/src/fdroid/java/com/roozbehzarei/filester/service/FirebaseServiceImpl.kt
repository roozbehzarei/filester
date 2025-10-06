package com.roozbehzarei.filester.service

import android.content.Context
import com.roozbehzarei.filester.domain.service.FirebaseService
import org.koin.core.annotation.Factory

@Factory(binds = [FirebaseService::class])
class FirebaseServiceImpl : FirebaseService {

    override fun setAnalyticsCollectionEnabled(context: Context, isEnabled: Boolean) {}

    override fun setCrashlyticsCollectionEnabled(isEnabled: Boolean) {}

}