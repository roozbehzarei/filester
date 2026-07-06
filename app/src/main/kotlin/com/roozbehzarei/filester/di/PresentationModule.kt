package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.presentation.screens.about.AboutViewModel
import com.roozbehzarei.filester.presentation.screens.main.MainViewModel
import com.roozbehzarei.filester.presentation.screens.settings.SettingsViewModel
import com.roozbehzarei.filester.presentation.state.UploadFabStateHolder
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val presentationModule = module {
    viewModel<MainViewModel>()
    viewModel<SettingsViewModel>()
    viewModel<AboutViewModel>()
    single<UploadFabStateHolder>()
}