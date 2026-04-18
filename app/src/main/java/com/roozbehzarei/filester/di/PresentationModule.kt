package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.presentation.screens.main.MainViewModel
import com.roozbehzarei.filester.presentation.state.UploadFabStateHolder
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val presentationModule = module {
    viewModel<MainViewModel>()
    single<UploadFabStateHolder>()
}