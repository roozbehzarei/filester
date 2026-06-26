val appVersionName = "3.0.0-rc01"
val isGlobalBuild = providers
    .gradleProperty("isGlobalBuild")
    .map { it.toBoolean() }
    .orElse(true)
    .get()

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

if (isGlobalBuild) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 24
        targetSdk = 37
        versionCode = 15
        versionName = appVersionName

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
            versionNameSuffix = "+global"
        }
        create("fdroid") {
            dimension = "store"
            versionNameSuffix = "+fdroid"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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

room {
    schemaDirectory("$projectDir/schemas")
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}

// Dokka configuration for generating project documentation
dokka {
    moduleName.set("Filester")
    moduleVersion.set(appVersionName)
    dokkaPublications.html {
        // Suppress inherited members (e.g., functions from Any, Object, etc.)
        suppressInheritedMembers.set(true)
        // Fail the build on Dokka warnings
        failOnWarning.set(true)
        // Set output directory
        outputDirectory.set(layout.projectDirectory.dir("../docs/"))
    }
    dokkaSourceSets.configureEach {
        suppress.set(name != "globalRelease")

        if (name == "globalRelease") {
            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl("https://github.com/roozbehzarei/filester/tree/main/app/src/main/java")
                remoteLineSuffix.set("#L")
            }
        }

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
    // Media3
    implementation(libs.androidx.media3.common.ktx)
    // ACRA
    "fdroidImplementation"(libs.acra.mail)
    "fdroidImplementation"(libs.acra.notification)
    // Firebase
    "globalImplementation"(platform(libs.firebase.bom))
    "globalImplementation"(libs.firebase.analytics)
    "globalImplementation"(libs.firebase.crashlytics)
    "globalImplementation"(libs.firebase.perf)
    // dokka
    dokkaPlugin(libs.android.documentation.plugin)
}