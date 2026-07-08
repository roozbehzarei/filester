## Overview

Filester is a secure, privacy-first, ad-free cloud storage application for Android that uploads
files to third-party cloud storage services for temporary hosting.

### 1. Designs and Patterns

- **Presentation Layer**: Built completely in Jetpack Compose.
    - ViewModels emit state via Kotlin `StateFlow` and handle incoming user events.
    - Navigation uses type-safe route objects (`MainRoute`, `SettingsRoute`, `AboutRoute`) with
      Jetpack Navigation Compose.
- **Domain Layer**: Clean Kotlin components containing business logic models, repository interfaces,
  and service definitions.
- **Data Layer**: Coordinates data flows between local SQLite database, network APIs, and settings
  preferences.

**Instruction**: Always follow Google-recommended architectural and design patterns

### 2. Dependency Injection

- Powered by Koin, using Compiler Plugin DSL approach which provides auto-wiring and compile-time
  safety. Setup modules are declared
  under di package.

### 3. Background Upload & Notifications

- Upload operations are executed via
  `WorkManager` (UploadWorker.kt)
  running as a Foreground Service with type `ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC`.
- Real-time progress updates are shown in notification drawer
  through UploadNotificationHelper.kt
  and can be canceled
  via FilesterBroadcastReceiver.kt.

### 4. Build Configuration

Gradle build scripts are written in Kotlin DSL. Also, the project divides flavor-specific
dependencies and source implementations:

- **`proprietary` Flavor**: Depends on Firebase (Analytics) and Kotzilla monitoring. Built with `-PisProprietaryDistribution=true` argument.
- **`foss` Flavor**: Exclusive to F-Droid/IzzyOnDroid distribution.
  Uses a stubbed/no-op implementation of AnalyticsServiceImpl.kt. Does not instantiate Kotzilla monitoring via `KoinApplication.setupMonitoring() {}` extension function in Extensions.kt. 