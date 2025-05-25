package com.roozbehzarei.filester.data.network.filester

import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val name: String,
    val apiKey: String
)