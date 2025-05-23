package com.roozbehzarei.filester.data.network.filester

import com.roozbehzarei.filester.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import org.koin.core.annotation.Single

private const val BASE_URL = "https://gist.githubusercontent.com/roozbehzarei"

@Single
class FilesterApi {

    private val client = HttpClient(CIO) {
        expectSuccess = true
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
    }

    suspend fun getVersion(): FilesterResponse {
        try {
            val config: RemoteConfigDto =
                client.get("${BASE_URL}/245781d48b1a3183eb59d99d25ee2bd3/raw/filester_remote_config.json")
                    .body()
            return FilesterResponse.Success(config)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            return FilesterResponse.Error
        }
    }

}