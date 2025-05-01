package com.roozbehzarei.filester.domain.model

data class File(
    val id: Int = 0,
    val name: String,
    val downloadUrl: String,
    val size: Long,
    val mimeType: String?,
)
