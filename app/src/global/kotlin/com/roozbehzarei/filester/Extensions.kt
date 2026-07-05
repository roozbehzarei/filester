package com.roozbehzarei.filester

import io.kotzilla.generated.monitoring
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