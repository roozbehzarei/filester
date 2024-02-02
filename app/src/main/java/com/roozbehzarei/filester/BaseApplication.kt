package com.roozbehzarei.filester

import android.app.Application
import com.aptabase.Aptabase
import com.roozbehzarei.filester.database.FileDatabase

private const val APTABASE_KEY = "A-EU-5566501326"

class BaseApplication : Application() {
    // Create database when needed
    val database: FileDatabase by lazy {
        FileDatabase.getDatabase(this)
    }


    override fun onCreate() {
        super.onCreate()
        // Initialize Aptabase SDK
        Aptabase.instance.initialize(applicationContext, APTABASE_KEY)
        // Track app launch on startup
        Aptabase.instance.trackEvent("app_started")
    }
}