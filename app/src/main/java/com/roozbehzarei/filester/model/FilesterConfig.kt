package com.roozbehzarei.filester.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilesterConfig(
    val versionCode: Int, val downloadUrl: String
)