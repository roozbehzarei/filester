/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

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
        CustomTabsIntent
            .Builder()
            .setColorScheme(
                if (isDarkTheme) {
                    CustomTabsIntent.COLOR_SCHEME_DARK
                } else {
                    CustomTabsIntent.COLOR_SCHEME_LIGHT
                },
            ).build()
    }
}
