package com.roozbehzarei.filester.data.mapper

import com.roozbehzarei.filester.data.network.filester.RemoteConfigDto
import com.roozbehzarei.filester.domain.model.RemoteConfig

fun RemoteConfigDto.toRemoteConfig(): RemoteConfig = RemoteConfig(versionCode = versionCode)

fun RemoteConfig.toRemoteConfigDto(): RemoteConfigDto = RemoteConfigDto(versionCode = versionCode)