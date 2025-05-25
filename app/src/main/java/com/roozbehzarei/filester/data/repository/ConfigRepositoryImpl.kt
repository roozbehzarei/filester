package com.roozbehzarei.filester.data.repository

import com.roozbehzarei.filester.data.mapper.toRemoteConfig
import com.roozbehzarei.filester.data.network.filester.FilesterApi
import com.roozbehzarei.filester.data.network.filester.FilesterResult
import com.roozbehzarei.filester.domain.model.RemoteConfig
import com.roozbehzarei.filester.domain.repository.ConfigRepository
import org.koin.core.annotation.Single

const val APTABASE_SERVICE_NAME = "Aptabase"

@Single
class ConfigRepositoryImpl(private val api: FilesterApi) : ConfigRepository {

    override suspend fun fetchRemoteConfig(): RemoteConfig? {
        val result = api.fetchRemoteConfig()
        return when (result) {
            is FilesterResult.Success -> result.config.toRemoteConfig()
            is FilesterResult.Error -> null
        }
    }

}