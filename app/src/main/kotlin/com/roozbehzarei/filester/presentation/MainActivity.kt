package com.roozbehzarei.filester.presentation

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
import com.roozbehzarei.filester.data.repository.UserPreferencesRepositoryImpl
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme
import org.koin.android.ext.android.inject


/**
 * Main Activity and entry point for the app.
 */
class MainActivity : AppCompatActivity() {

    private val userPreferencesRepository: UserPreferencesRepositoryImpl by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDynamicColor by userPreferencesRepository.getDynamicColorsPreference()
                .collectAsState(
                    false
                )
            val userThemePreference by userPreferencesRepository.getThemePreference()
                .collectAsState(Theme.Default)
            val view = LocalView.current
            val insetsController = WindowCompat.getInsetsController(this@MainActivity.window, view)
            // Dynamically modify the foreground color of status bar to align with app theme
            when (userThemePreference) {
                Theme.Light -> insetsController.isAppearanceLightStatusBars = true
                Theme.Dark -> insetsController.isAppearanceLightStatusBars = false
                Theme.Default -> insetsController.isAppearanceLightStatusBars =
                    isSystemInDarkTheme().not()
            }
            FilesterAppTheme(
                dynamicColor = isDynamicColor,
                darkTheme = when (userThemePreference) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.Default -> isSystemInDarkTheme()
                }
            ) {
                FilesterApp(context = this)
            }
        }
    }

}