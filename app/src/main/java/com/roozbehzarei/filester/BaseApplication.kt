package com.roozbehzarei.filester

import android.app.Application
import android.content.Context
import com.roozbehzarei.filester.di.databaseModule
import com.roozbehzarei.filester.di.networkModule
import com.roozbehzarei.filester.di.presentationModule
import com.roozbehzarei.filester.di.repositoryModule
import com.roozbehzarei.filester.di.serviceModule
import com.roozbehzarei.filester.di.workerModule
import com.roozbehzarei.filester.service.AcraServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

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
            workManagerFactory()
            modules(
                databaseModule, networkModule, repositoryModule, serviceModule, presentationModule,
                workerModule
            )
        }

    }

}