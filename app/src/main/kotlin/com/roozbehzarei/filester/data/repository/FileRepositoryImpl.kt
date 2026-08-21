package com.roozbehzarei.filester.data.repository

import com.roozbehzarei.filester.data.local.FileDao
import com.roozbehzarei.filester.data.mapper.toFile
import com.roozbehzarei.filester.data.mapper.toFileEntity
import com.roozbehzarei.filester.data.network.litterbox.LitterboxApi
import com.roozbehzarei.filester.data.network.uguu.UguuApi
import com.roozbehzarei.filester.data.network.x0.X0Api
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.domain.model.HostProvider
import com.roozbehzarei.filester.domain.model.UploadResult
import com.roozbehzarei.filester.domain.repository.FileRepository
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FileRepositoryImpl(
    private val fileDao: FileDao,
    private val litterboxApi: Lazy<LitterboxApi>,
    private val uguuApi: Lazy<UguuApi>,
    private val x0Api: Lazy<X0Api>,
) : FileRepository {
    override fun getFiles(): Flow<List<File>> = fileDao.getAll().map { entities -> entities.map { entity -> entity.toFile() } }

    override fun uploadFile(
        file: PlatformFile,
        hostProvider: HostProvider,
    ): Flow<UploadResult<String>> =
        when (hostProvider) {
            HostProvider.LITTERBOX -> litterboxApi.value.uploadFile(file)
            HostProvider.UGUU -> uguuApi.value.uploadFile(file)
            HostProvider.X0 -> x0Api.value.uploadFile(file)
        }

    override suspend fun saveFile(file: File) {
        fileDao.insert(file.toFileEntity())
    }

    override suspend fun deleteFile(file: File) {
        fileDao.delete(file.toFileEntity())
    }
}
