package com.roozbehzarei.filester.database

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Version(
    val code: Int, val url: String
)