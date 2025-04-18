package com.roozbehzarei.filester.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.data.local.UserPreferencesRepository
import com.roozbehzarei.filester.ui.theme.FilesterAppTheme
import org.koin.android.ext.android.inject


/**
 * Main Activity and entry point for the app.
 */
class MainActivity : AppCompatActivity() {

    private val userPreferencesRepository: UserPreferencesRepository by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDynamicColor by userPreferencesRepository.getDynamicColorsPreference.collectAsState(
                false
            )
            val userThemePreference by userPreferencesRepository.getThemePreference.collectAsState(1)
            val view = LocalView.current
            val insetsController = WindowCompat.getInsetsController(this@MainActivity.window, view)
            // Dynamically modify the foreground color of status bar to align with app theme
            when (userThemePreference) {
                // Light mode
                0 -> {
                    insetsController.isAppearanceLightStatusBars = true
                }
                // Dark mode
                2 -> {
                    insetsController.isAppearanceLightStatusBars = false
                }
            }
            FilesterAppTheme(
                dynamicColor = isDynamicColor,
                darkTheme = when (userThemePreference) {
                    0 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                }
            ) {
                FilesterApp(context = this)
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id), name, importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}