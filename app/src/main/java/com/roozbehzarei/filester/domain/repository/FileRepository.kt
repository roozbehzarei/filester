package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.domain.model.File
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    fun getFiles(): Flow<List<File>>

    suspend fun deleteFile(file: File)

}