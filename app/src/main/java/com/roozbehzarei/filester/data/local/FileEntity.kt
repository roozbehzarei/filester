package com.roozbehzarei.filester.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    val size: Long,
    val mimeType: String?,
)