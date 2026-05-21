package com.roozbehzarei.filester.service

import android.app.Application
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.service.AcraService
import org.acra.BuildConfig
import org.acra.config.mailSender
import org.acra.config.notification
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class AcraServiceImpl : AcraService {

    override fun initialize(application: Application) {
        application.initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.KEY_VALUE_LIST
            with(application.baseContext) {
                notification {
                    title = getString(R.string.crash_title_notification)
                    text = getString(R.string.crash_text_notification)
                    channelName = getString(R.string.notification_channel_id)
                    resIcon = R.drawable.ic_notification
                    sendButtonText = getString(R.string.crash_button_send)
                    discardButtonText = getString(R.string.cancel)
                    sendOnClick = true
                }
                mailSender {
                    mailTo = "contact@roozbehzarei.com"
                    subject = getString(R.string.crash_text_mail_subject)
                    body = getString(R.string.crash_text_mail_body)
                }
            }
        }
    }

}