package com.roozbehzarei.filester

import android.app.Application
import android.content.Context
import com.roozbehzarei.filester.di.AppModule
import com.roozbehzarei.filester.domain.repository.AptabaseAnalyticsRepository
import org.acra.BuildConfig
import org.acra.config.mailSender
import org.acra.config.notification
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class BaseApplication : Application() {

    private val aptabaseAnalyticsRepository: AptabaseAnalyticsRepository by inject()

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(AppModule().module)
        }

        // Initialize analytics
        aptabaseAnalyticsRepository.initialize(applicationContext)

    }

    /**
     * Configure ACRA
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.KEY_VALUE_LIST
            notification {
                title = getString(R.string.crash_notification_title)
                text = getString(R.string.crash_notification_text)
                channelName = getString(R.string.notification_channel_id)
                resIcon = R.drawable.ic_notification
                sendButtonText = getString(R.string.send)
                discardButtonText = getString(R.string.button_cancel)
                sendOnClick = true
            }
            mailSender {
                mailTo = "contact@roozbehzarei.com"
                subject = getString(R.string.crash_mail_subject)
                body = getString(R.string.crash_mail_body)
            }
        }
    }
}