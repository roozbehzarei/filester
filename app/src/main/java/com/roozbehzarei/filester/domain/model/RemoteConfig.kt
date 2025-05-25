package com.roozbehzarei.filester.domain.model

data class RemoteConfig(
    val appVersionCode: Int, val services: List<Service>
)