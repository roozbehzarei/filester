package com.roozbehzarei.filester

import android.app.Application
import android.content.Context
import com.roozbehzarei.filester.di.databaseModule
import com.roozbehzarei.filester.di.networkModule
import com.roozbehzarei.filester.di.presentationModule
import com.roozbehzarei.filester.di.repositoryModule
import com.roozbehzarei.filester.di.serviceModule
import com.roozbehzarei.filester.di.workerModule
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import com.roozbehzarei.filester.domain.service.FirebaseService
import com.roozbehzarei.filester.service.AcraServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

/**
 * The main entry point for the application process.
 */
class BaseApplication : Application(), KoinComponent {

    val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val userPreferencesRepository: UserPreferencesRepository by inject()
    val firebaseService: FirebaseService by inject()

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
                databaseModule,
                networkModule,
                repositoryModule,
                serviceModule,
                presentationModule,
                workerModule
            )
        }

        applicationScope.launch {
            userPreferencesRepository.getTelemetryPreference().collect { isEnabled ->
                firebaseService.setAnalyticsCollectionEnabled(isEnabled)
            }
        }
        applicationScope.launch {
            userPreferencesRepository.getCrashReportPreference().collect { isEnabled ->
                firebaseService.setCrashlyticsCollectionEnabled(isEnabled)
            }
        }
    }

}