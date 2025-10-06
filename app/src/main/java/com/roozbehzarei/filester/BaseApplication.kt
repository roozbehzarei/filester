package com.roozbehzarei.filester

import android.app.Application
import android.content.Context
import com.roozbehzarei.filester.di.AppModule
import com.roozbehzarei.filester.service.AcraServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

/**
 * The main entry point for the application process.
 */
class BaseApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        // Initialize ACRA crash reporting
        val acraService = AcraServiceImpl()
        acraService.initialize(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin for dependency injection
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(AppModule().module)
        }

    }

}