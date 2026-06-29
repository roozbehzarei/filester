package com.roozbehzarei.filester.di

import android.content.Context
import androidx.work.WorkManager
import com.roozbehzarei.filester.upload.UploadManager
import com.roozbehzarei.filester.upload.UploadManagerImpl
import com.roozbehzarei.filester.upload.UploadNotificationHelper
import com.roozbehzarei.filester.upload.UploadWorker
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.worker

private fun provideWorkManager(context: Context) = WorkManager.getInstance(context)

val workerModule = module {
    single<UploadNotificationHelper>()
    factory<UploadManagerImpl>() bind UploadManager::class
    factory { create(::provideWorkManager) }
    worker<UploadWorker>()
}