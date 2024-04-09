package com.roozbehzarei.filester.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Version(
    val code: Int, val url: String
)