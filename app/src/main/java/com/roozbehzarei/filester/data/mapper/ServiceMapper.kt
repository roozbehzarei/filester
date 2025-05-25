package com.roozbehzarei.filester.data.mapper

import com.roozbehzarei.filester.data.network.filester.ServiceDto
import com.roozbehzarei.filester.domain.model.Service

fun ServiceDto.toService(): Service = Service(name = name, apiKey = apiKey)

fun Service.toServiceDto(): ServiceDto = ServiceDto(name = name, apiKey = apiKey)