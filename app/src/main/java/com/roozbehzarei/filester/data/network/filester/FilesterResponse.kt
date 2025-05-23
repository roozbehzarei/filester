package com.roozbehzarei.filester.data.network.filester

import kotlinx.serialization.Serializable

sealed class FilesterResponse {

    data class Success(val config: RemoteConfigDto) : FilesterResponse()
    data object Error : FilesterResponse()

}

@Serializable
data class RemoteConfigDto(
    val versionCode: Int
)