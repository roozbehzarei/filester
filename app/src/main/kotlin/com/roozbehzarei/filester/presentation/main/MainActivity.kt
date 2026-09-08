package com.roozbehzarei.filester.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.presentation.FilesterApp
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main Activity and entry point for the app.
 */
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.isLoading }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isDarkTheme =
                when (uiState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.Default -> isSystemInDarkTheme()
                }
            val view = LocalView.current
            SideEffect {
                val insetsController = WindowCompat.getInsetsController(this@MainActivity.window, view)
                insetsController.isAppearanceLightStatusBars = isDarkTheme.not()
            }
            FilesterAppTheme(
                dynamicColor = uiState.isDynamicColor,
                darkTheme = isDarkTheme,
            ) {
                FilesterApp(context = this)
            }
        }
    }
}
