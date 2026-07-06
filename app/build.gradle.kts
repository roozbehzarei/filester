val appVersionName = "3.0.2"
val isProprietaryDistribution =
    providers.gradleProperty("isProprietaryDistribution")
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

if (isProprietaryDistribution) {
    apply(plugin = libs.plugins.kotzilla.get().pluginId)
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 24
        targetSdk = 37
        versionCode = 18
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += listOf("distribution")

    productFlavors {
        create("proprietary") {
            dimension = "distribution"
        }
        create("foss") {
            dimension = "distribution"
            versionNameSuffix = "-foss"
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
        suppress.set(name != "proprietaryRelease")

        if (name == "proprietaryRelease") {
            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl("https://github.com/roozbehzarei/filester/tree/main/app/src/main/java")
                remoteLineSuffix.set("#L")
            }
        }

        // Suppress generated files
        suppressGeneratedFiles.set(true)
        // Define options for specific packages, overriding proprietary settings
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
    // Firebase
    "proprietaryImplementation"(platform(libs.firebase.bom))
    "proprietaryImplementation"(libs.firebase.analytics)
    // dokka
    dokkaPlugin(libs.android.documentation.plugin)
}