package com.roozbehzarei.filester.data.repository

import com.roozbehzarei.filester.data.mapper.toRemoteConfig
import com.roozbehzarei.filester.data.network.filester.FilesterApi
import com.roozbehzarei.filester.data.network.filester.FilesterResponse
import com.roozbehzarei.filester.domain.model.RemoteConfig
import com.roozbehzarei.filester.domain.repository.ConfigRepository
import org.koin.core.annotation.Single

@Single
class ConfigRepositoryImpl(private val api: FilesterApi) : ConfigRepository {

    override suspend fun fetchRemoteConfig(): RemoteConfig? {
        val result = api.getVersion()
        return when (result) {
            is FilesterResponse.Success -> result.config.toRemoteConfig()
            is FilesterResponse.Error -> null
        }
    }

}