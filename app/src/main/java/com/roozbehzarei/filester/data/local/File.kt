package com.roozbehzarei.filester.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class File(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val url: String,
    val size: Long,
    val mimeType: String?,
)