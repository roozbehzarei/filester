plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.dokka) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotzilla) apply true
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}