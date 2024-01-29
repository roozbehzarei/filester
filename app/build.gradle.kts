plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 21
        targetSdk = 34
        versionCode = 5
        versionName = "2.2.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    androidResources {
        generateLocaleConfig = true
    }
}

dependencies {
    val navVersion = "2.5.3"
    val activityVersion = "1.7.2"
    val fragmentVersion = "1.6.1"
    val lifecycleVersion = "2.6.1"
    val retrofitVersion = "2.9.0"
    val workVersion = "2.8.1"
    val roomVersion = "2.5.2"
    val preferenceVersion = "1.2.0"

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // Fragment
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Retrofit with Scalars Converter
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
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
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")
    // Preference
    implementation("androidx.preference:preference-ktx:$preferenceVersion")
}