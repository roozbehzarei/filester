package com.roozbehzarei.filester.di

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.roozbehzarei.filester.presentation.navigation.AboutRoute
import com.roozbehzarei.filester.presentation.navigation.MainRoute
import com.roozbehzarei.filester.presentation.navigation.SettingsRoute
import com.roozbehzarei.filester.presentation.screens.about.AboutScreen
import com.roozbehzarei.filester.presentation.screens.main.MainScreen
import com.roozbehzarei.filester.presentation.screens.main.MainViewModel
import com.roozbehzarei.filester.presentation.screens.settings.SettingsScreen
import com.roozbehzarei.filester.presentation.screens.settings.SettingsViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.viewModel

@OptIn(KoinExperimentalAPI::class)
val presentationModule =
    module {
        viewModel<MainViewModel>()
        viewModel<SettingsViewModel>()

        navigation<MainRoute> {
            MainScreen(modifier = Modifier.fillMaxSize())
        }
        navigation<SettingsRoute> {
            SettingsScreen(modifier = Modifier.fillMaxSize())
        }
        navigation<AboutRoute> {
            AboutScreen(modifier = Modifier.fillMaxSize())
        }
    }
