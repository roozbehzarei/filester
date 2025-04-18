package com.roozbehzarei.filester.data.network.filester

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FilesterConfig(
    val versionCode: Int, val downloadUrl: String
)