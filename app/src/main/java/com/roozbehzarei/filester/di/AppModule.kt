package com.roozbehzarei.filester.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DatabaseModule::class, NetworkModule::class, RepositoryModule::class, UiModule::class])
@ComponentScan
class AppModule