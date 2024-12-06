plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 21
        targetSdk = 35
        versionCode = 9
        versionName = "2.3.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    androidResources {
        generateLocaleConfig = true
    }
}

dependencies {
    val navVersion = "2.8.4"
    val activityVersion = "1.9.3"
    val fragmentVersion = "1.8.5"
    val lifecycleVersion = "2.8.7"
    val retrofitVersion = "2.9.0"
    val workVersion = "2.10.0"
    val roomVersion = "2.6.1"
    val preferenceVersion = "1.2.1"
    val acraVersion = "5.11.3"

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    // Fragment
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Retrofit 2
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Activity
    implementation("androidx.activity:activity-ktx:$activityVersion")
    // SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")
    // Preference
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
    // Browser
    implementation("androidx.browser:browser:1.8.0")
    // Aptabase
    implementation("com.github.aptabase:aptabase-kotlin:0.0.8")
    // ACRA
    implementation("ch.acra:acra-mail:$acraVersion")
    implementation("ch.acra:acra-notification:$acraVersion")
}