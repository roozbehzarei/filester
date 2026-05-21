import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val versionName = "3.0.0-alpha04"

// Get the list of all Gradle tasks requested by the invoked build
val taskNames = gradle.startParameter.taskNames
// Only apply Google Services and Firebase plugins if we are NOT running a build for F-Droid or generating Dokka documentation.
if (taskNames.all {
        it.contains("fdroid", ignoreCase = true).not() && it.contains(
            "dokka", ignoreCase = true
        ).not()
    }) {
    with(pluginManager) {
        apply(libs.plugins.google.services.get().pluginId)
        apply(libs.plugins.firebase.crashlytics.get().pluginId)
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 24
        targetSdk = 36
        versionCode = 14
        versionName = versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += listOf("store")

    productFlavors {
        create("global") {
            dimension = "store"
        }
        create("fdroid") {
            dimension = "store"
            applicationIdSuffix = ".fdroid"
            versionNameSuffix = " (f-droid)"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        generateLocaleConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}

// Dokka configuration for generating project documentation
dokka {
    moduleName.set("Filester")
    moduleVersion.set(versionName)
    dokkaPublications.html {
        // Suppress inherited members (e.g., functions from Any, Object, etc.)
        suppressInheritedMembers.set(true)
        // Fail the build on Dokka warnings
        failOnWarning.set(true)
    }
    dokkaSourceSets.main {
        // Configure source linking to GitHub for better navigation in the generated docs
        sourceLink {
            localDirectory.set(file("src/main/java"))
            remoteUrl("https://github.com/roozbehzarei/filester/tree/main/app/src/main/java")
            remoteLineSuffix.set("#L")
        }
    }
    dokkaSourceSets.all {
        // Suppress generated files
        suppressGeneratedFiles.set(true)
        // Define options for specific packages, overriding global settings
        perPackageOption {
            // Regex matching Koin’s KSP-generated code packages
            matchingRegex.set("org\\.koin\\.ksp\\.generated(\\..*)?")
            // Hide any code in packages that match the regex
            suppress.set(true)
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.material.icons.extended)
    // Activity
    implementation(libs.androidx.activity.compose)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)
    // SplashScreen
    implementation(libs.androidx.core.splashscreen)
    // Google Fonts
    implementation(libs.androidx.ui.text.google.fonts)
    // Browser
    implementation(libs.androidx.browser)
    // DocumentFile
    implementation(libs.androidx.documentfile)
    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)
    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.androidx.workmanager)
    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    // Apache Log4j
    implementation(libs.slf4j.android)
    // Accompanist
    implementation(libs.accompanist.permissions)
    // ACRA
    "fdroidImplementation"(libs.acra.mail)
    "fdroidImplementation"(libs.acra.notification)
    // Media3
    implementation(libs.androidx.media3.common.ktx)
    // Firebase
    "globalImplementation"(platform(libs.firebase.bom))
    "globalImplementation"(libs.firebase.analytics)
    "globalImplementation"(libs.firebase.crashlytics)
    "globalImplementation"(libs.firebase.perf)
    // dokka
    dokkaPlugin(libs.android.documentation.plugin)
}