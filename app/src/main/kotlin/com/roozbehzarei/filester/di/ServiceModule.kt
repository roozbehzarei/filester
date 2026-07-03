package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.domain.service.FirebaseService
import com.roozbehzarei.filester.service.FirebaseServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory

val serviceModule = module {
    factory<FirebaseServiceImpl>() bind FirebaseService::class
}