package com.roozbehzarei.filester

import org.koin.core.KoinApplication

fun KoinApplication.setupMonitoring() {}

fun getMonitoringConsent(): Boolean = false

fun setMonitoringConsent(isEnabled: Boolean) {}

fun seedMonitoringConsent() {}
