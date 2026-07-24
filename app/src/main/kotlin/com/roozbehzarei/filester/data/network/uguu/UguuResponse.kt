package com.roozbehzarei.filester.data.network.uguu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UguuResponse(
    val success: Boolean,
    val files: List<UguuFileItem>? = null
)

@Serializable
data class UguuFileItem(
    @SerialName("hash") val hash: String? = null,
    @SerialName("filename") val name: String? = null,
    @SerialName("url") val url: String
)