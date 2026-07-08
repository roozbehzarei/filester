package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.data.repository.FileRepositoryImpl
import com.roozbehzarei.filester.data.repository.UserPreferencesRepositoryImpl
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val repositoryModule = module {
    single<FileRepositoryImpl>() bind FileRepository::class
    single<UserPreferencesRepositoryImpl>() bind UserPreferencesRepository::class
}