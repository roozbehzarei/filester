// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id ("org.jetbrains.kotlin.android") version "2.0.21" apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}