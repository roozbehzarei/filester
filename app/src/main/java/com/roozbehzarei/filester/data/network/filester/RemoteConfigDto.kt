package com.roozbehzarei.filester.data.network.filester

import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigDto(
    val appVersionCode: Int,
    val services: List<ServiceDto>
)