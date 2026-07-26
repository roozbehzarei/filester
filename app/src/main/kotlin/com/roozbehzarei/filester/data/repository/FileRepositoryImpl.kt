package com.roozbehzarei.filester.data.repository

import com.roozbehzarei.filester.data.local.FileDao
import com.roozbehzarei.filester.data.mapper.toFile
import com.roozbehzarei.filester.data.mapper.toFileEntity
import com.roozbehzarei.filester.data.network.litterbox.LitterboxApi
import com.roozbehzarei.filester.data.network.uguu.UguuApi
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.RemoteResource
import com.roozbehzarei.filester.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FileRepositoryImpl(
    private val fileDao: FileDao,
    private val litterboxApi: LitterboxApi,
    private val uguuApi: UguuApi
) : FileRepository {

    override fun getFiles(): Flow<List<File>> =
        fileDao.getAll().map { entities -> entities.map { entity -> entity.toFile() } }

    override fun uploadFile(
        file: java.io.File,
        hostProvider: HostProvider
    ): Flow<RemoteResource<String>> =
        when (hostProvider) {
            HostProvider.LITTERBOX -> litterboxApi.uploadFile(file)
            HostProvider.UGUU -> uguuApi.uploadFile(file)
        }

    override suspend fun saveFile(file: File) {
        fileDao.insert(file.toFileEntity())
    }

    override suspend fun deleteFile(file: File) {
        fileDao.delete(file.toFileEntity())
    }

}