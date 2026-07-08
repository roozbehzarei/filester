package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.presentation.screens.about.AboutViewModel
import com.roozbehzarei.filester.presentation.screens.main.MainViewModel
import com.roozbehzarei.filester.presentation.screens.settings.SettingsViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val presentationModule = module {
    viewModel<MainViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<AboutViewModel>()
}