package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.data.network.catbox.CatboxApi
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val networkModule = module {
    single<CatboxApi>()
}