package com.roozbehzarei.filester.di

import androidx.compose.material3.SnackbarHostState
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.roozbehzarei.filester.presentation")
class UiModule

@Single
fun provideSnackbarHostState() = SnackbarHostState()