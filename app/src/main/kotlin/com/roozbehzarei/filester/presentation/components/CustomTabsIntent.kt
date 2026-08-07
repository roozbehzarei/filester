package com.roozbehzarei.filester.presentation.components

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.roozbehzarei.filester.presentation.theme.LocalIsDarkTheme

/**
 * Remembers a [CustomTabsIntent] whose color scheme follows the app's theme rather than
 * the system's, so a Custom Tab matches the user's theme preference.
 */
@Composable
fun rememberCustomTabsIntent(): CustomTabsIntent {
    val isDarkTheme = LocalIsDarkTheme.current
    return remember(isDarkTheme) {
        CustomTabsIntent.Builder().setColorScheme(
            if (isDarkTheme) CustomTabsIntent.COLOR_SCHEME_DARK
            else CustomTabsIntent.COLOR_SCHEME_LIGHT
        ).build()
    }
}
