plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlinx-serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 24
        targetSdk = 36
        versionCode = 11
        versionName = "3.0.0-alpha01"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    val navVersion = "2.8.9"
    val retrofitVersion = "2.9.0"
    val workVersion = "2.10.0"
    val roomVersion = "2.7.0"
    val acraVersion = "5.12.0"
    val accompanistVersion = "0.37.2"
    val media3_version = "1.6.1"


    // Core
    implementation("androidx.core:core-ktx:1.16.0")
    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2025.03.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.material:material-icons-extended")
    // Activity
    implementation("androidx.activity:activity-compose:1.10.1")
    // Navigation
    implementation("androidx.navigation:navigation-compose:$navVersion")
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.4")
    // SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")
    // Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")
    // Browser
    implementation("androidx.browser:browser:1.8.0")
    // DocumentFile
    implementation("androidx.documentfile:documentfile:1.0.1")
    // kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    // Koin
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.0.2"))
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-androidx-compose")
    implementation("io.insert-koin:koin-annotations:2.0.0")
    ksp("io.insert-koin:koin-ksp-compiler:2.0.0")
    // Accompanist
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    // Retrofit 2
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    // Aptabase
    implementation("com.github.aptabase:aptabase-kotlin:0.0.8")
    // ACRA
    implementation("ch.acra:acra-mail:$acraVersion")
    implementation("ch.acra:acra-notification:$acraVersion")
    // Media3
    implementation("androidx.media3:media3-common-ktx:$media3_version")
    // Apache Commons IO
    implementation("commons-io:commons-io:2.19.0")
}