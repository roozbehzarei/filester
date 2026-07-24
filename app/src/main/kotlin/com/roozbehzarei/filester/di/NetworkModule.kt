package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.data.network.catbox.CatboxApi
import com.roozbehzarei.filester.data.network.uguu.UguuApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single

private fun createHttpClient(): HttpClient =
    HttpClient(CIO) {
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.HEADERS
            }
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15000L
            socketTimeoutMillis = 30000L
            requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        }
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }
    }

val networkModule = module {
    single { create(::createHttpClient) }
    single<CatboxApi>()
    single<UguuApi>()
}