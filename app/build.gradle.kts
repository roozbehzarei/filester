plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.roozbehzarei.filester"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.roozbehzarei.filester"
        minSdk = 21
        targetSdk = 34
        versionCode = 6
        versionName = "2.3.0"

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
    // Webkit
    implementation("androidx.webkit:webkit:1.10.0")
    // Aptabase
    implementation("com.aptabase:aptabase:0.0.6")
}