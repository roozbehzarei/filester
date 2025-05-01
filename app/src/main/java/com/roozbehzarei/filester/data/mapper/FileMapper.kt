package com.roozbehzarei.filester.data.mapper

import com.roozbehzarei.filester.data.local.FileEntity
import com.roozbehzarei.filester.domain.model.File

fun FileEntity.toFile(): File = File(
    id = id, name = name, downloadUrl = url, size = size, mimeType = mimeType
)

fun File.toFileEntity(): FileEntity = FileEntity(
    id = id, name = name, url = downloadUrl, size = size, mimeType = mimeType
)