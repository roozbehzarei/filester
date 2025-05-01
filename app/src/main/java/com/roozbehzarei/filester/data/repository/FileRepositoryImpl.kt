package com.roozbehzarei.filester.data.repository

import com.roozbehzarei.filester.data.local.FileDao
import com.roozbehzarei.filester.data.mapper.toFile
import com.roozbehzarei.filester.data.mapper.toFileEntity
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class FileRepositoryImpl(private val fileDao: FileDao) : FileRepository {

    override fun getFiles(): Flow<List<File>> =
        fileDao.getAll().map { entities -> entities.map { entity -> entity.toFile() } }

    override suspend fun deleteFile(file: File) {
        fileDao.delete(file.toFileEntity())
    }

}