package com.roozbehzarei.filester.data.mapper

import com.roozbehzarei.filester.data.network.filester.RemoteConfigDto
import com.roozbehzarei.filester.domain.model.RemoteConfig

fun RemoteConfigDto.toRemoteConfig(): RemoteConfig = RemoteConfig(
    appVersionCode = this@toRemoteConfig.appVersionCode,
    services = this.services.map { dto -> dto.toService() })

fun RemoteConfig.toRemoteConfigDto(): RemoteConfigDto = RemoteConfigDto(
    appVersionCode = appVersionCode, this.services.map { key -> key.toServiceDto() })