package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.domain.model.RemoteConfig

interface ConfigRepository {

    suspend fun fetchRemoteConfig(): RemoteConfig?

}