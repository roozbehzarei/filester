package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.data.repository.FileRepositoryImpl
import com.roozbehzarei.filester.data.repository.UserPreferencesRepositoryImpl
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory

val repositoryModule = module {
    factory<FileRepositoryImpl>() bind FileRepository::class
    factory<UserPreferencesRepositoryImpl>() bind UserPreferencesRepository::class
}