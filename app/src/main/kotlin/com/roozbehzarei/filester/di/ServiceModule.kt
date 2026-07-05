package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.domain.service.AnalyticsService
import com.roozbehzarei.filester.service.AnalyticsServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory

val serviceModule = module {
    factory<AnalyticsServiceImpl>() bind AnalyticsService::class
}