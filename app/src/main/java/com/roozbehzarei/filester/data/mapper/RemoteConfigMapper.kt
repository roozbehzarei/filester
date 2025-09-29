package com.roozbehzarei.filester.data.mapper

import com.roozbehzarei.filester.data.network.filester.RemoteConfigDto
import com.roozbehzarei.filester.domain.model.RemoteConfig

fun RemoteConfigDto.toRemoteConfig(): RemoteConfig =
    RemoteConfig(latestVersionCode = latestVersionCode, minVersionCode = minVersionCode)

fun RemoteConfig.toRemoteConfigDto(): RemoteConfigDto =
    RemoteConfigDto(latestVersionCode = latestVersionCode, minVersionCode = minVersionCode)