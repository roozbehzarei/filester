package com.roozbehzarei.filester.data.network.filester

import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigDto(
    val latestVersionCode: Int,
    val minVersionCode: Int
)