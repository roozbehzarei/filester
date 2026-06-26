package com.roozbehzarei.filester.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class File(
    val id: Int = 0,
    val name: String,
    val downloadUrl: String,
    val size: Long,
    val mimeType: String?,
    val uploadedAt: Long = 0L,
    val expiresAt: Long = 0L,
) : Parcelable