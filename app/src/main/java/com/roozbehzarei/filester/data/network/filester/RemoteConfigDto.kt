package com.roozbehzarei.filester.data.network.filester

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigDto(
    @SerialName("version_code_latest")
    val latestVersionCode: Int, @SerialName("version_code_min")
    val minVersionCode: Int
)