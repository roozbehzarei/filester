package com.roozbehzarei.filester.domain.repository

import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.model.RemoteResource
import kotlinx.coroutines.flow.Flow

interface FileRepository {

    fun getFiles(): Flow<List<File>>

    fun uploadFile(file: java.io.File): Flow<RemoteResource<String>>

    suspend fun saveFile(file: File)

    suspend fun deleteFile(file: File)

}