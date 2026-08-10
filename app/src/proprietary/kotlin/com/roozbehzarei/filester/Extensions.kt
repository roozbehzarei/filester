package com.roozbehzarei.filester

import io.kotzilla.generated.monitoring
import io.kotzilla.sdk.KotzillaConsent
import io.kotzilla.sdk.KotzillaSDK
import io.kotzilla.sdk.config.Environment
import org.koin.core.KoinApplication

fun KoinApplication.setupMonitoring() {
    monitoring {
        if (BuildConfig.DEBUG) {
            setEnvironment(Environment.Dev())
            setDebugBuild(true)
        } else {
            setEnvironment(Environment.Prod)
            setDebugBuild(false)
        }
    }
}

/**
 * Monitoring is opt-out, so a pending decision counts as enabled.
 * `KotzillaSDK.getConsent()` returns `null` while no decision has been recorded yet.
 */
fun getMonitoringConsent(): Boolean = KotzillaSDK.getConsent() != KotzillaConsent.NOT_GRANTED

fun setMonitoringConsent(isEnabled: Boolean) {
    KotzillaSDK.setConsent(
        if (isEnabled) KotzillaConsent.GRANTED else KotzillaConsent.NOT_GRANTED,
    )
}

/**
 * Records the default decision on first launch. Without it the consent gate would buffer
 * telemetry indefinitely for anyone who never opens the settings screen.
 */
fun seedMonitoringConsent() {
    if (KotzillaSDK.getConsent() == null) {
        KotzillaSDK.setConsent(KotzillaConsent.GRANTED)
    }
}
