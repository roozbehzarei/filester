package com.roozbehzarei.filester.data.network.filester

import com.roozbehzarei.filester.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

private const val BASE_URL = "https://gist.githubusercontent.com/roozbehzarei"

@Single
class FilesterApi {

    private val client = HttpClient(CIO) {
        expectSuccess = true
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    suspend fun fetchRemoteConfig(): FilesterResult {
        try {
            val response =
                client.get("${BASE_URL}/bc436b70634498bb59bca06cf0cab8df/raw/filester3_remote_config.json")
                    .bodyAsText()
            val config = json.decodeFromString<RemoteConfigDto>(response)
            return FilesterResult.Success(config)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            return FilesterResult.Error
        }
    }

}