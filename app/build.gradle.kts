plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

/**
 * Applies the Google Services plugin only if the build flavor is "fdroid",
 * determined by inspecting the Gradle task names.
 */
val tasks = gradle.startParameter.taskNames
if (tasks.any { it.contains("fdroid", ignoreCase = true).not() }) {
    with(pluginManager) {
        apply(libs.plugins.google.services.get().pluginId)
        apply(libs.plugins.firebase.crashlytics.get().pluginId)
    }
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 24
        targetSdk = 36
        versionCode = 14
        versionName = "3.0.0-alpha04"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    androidResources {
        generateLocaleConfig = true
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
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
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
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
}