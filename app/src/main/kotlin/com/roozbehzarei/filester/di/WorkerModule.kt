package com.roozbehzarei.filester.di

import android.content.Context
import androidx.work.WorkManager
import com.roozbehzarei.filester.framework.UploadWorker
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.worker

private fun provideWorkManager(context: Context) = WorkManager.getInstance(context)

val workerModule = module {
    factory { create(::provideWorkManager) }
    worker<UploadWorker>()
}