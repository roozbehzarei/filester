/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.di

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.roozbehzarei.filester.presentation.main.MainViewModel
import com.roozbehzarei.filester.presentation.navigation.AboutRoute
import com.roozbehzarei.filester.presentation.navigation.SettingsRoute
import com.roozbehzarei.filester.presentation.navigation.UploadsRoute
import com.roozbehzarei.filester.presentation.screens.about.AboutScreen
import com.roozbehzarei.filester.presentation.screens.settings.SettingsScreen
import com.roozbehzarei.filester.presentation.screens.settings.SettingsViewModel
import com.roozbehzarei.filester.presentation.screens.uploads.UploadsScreen
import com.roozbehzarei.filester.presentation.screens.uploads.UploadsViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.viewModel

@OptIn(KoinExperimentalAPI::class)
val presentationModule =
    module {
        viewModel<MainViewModel>()
        viewModel<UploadsViewModel>()
        viewModel<SettingsViewModel>()

        navigation<UploadsRoute> {
            UploadsScreen(modifier = Modifier.fillMaxSize())
        }
        navigation<SettingsRoute> {
            SettingsScreen(modifier = Modifier.fillMaxSize())
        }
        navigation<AboutRoute> {
            AboutScreen(modifier = Modifier.fillMaxSize())
        }
    }
