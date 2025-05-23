package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.data.network.catbox.CatboxResult
import com.roozbehzarei.filester.domain.model.File
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    fun getFiles(): Flow<List<File>>

    fun uploadFile(file: java.io.File): Flow<CatboxResult>

    suspend fun saveFile(file: File)

    suspend fun deleteFile(file: File)

}