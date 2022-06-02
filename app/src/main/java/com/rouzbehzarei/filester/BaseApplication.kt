package com.rouzbehzarei.filester

import android.app.Application
import com.rouzbehzarei.filester.database.FileDatabase

class BaseApplication : Application() {
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: FileDatabase by lazy {
        FileDatabase.getDatabase(this)
    }

}