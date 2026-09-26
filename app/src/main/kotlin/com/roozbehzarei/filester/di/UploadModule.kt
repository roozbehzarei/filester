/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.di

import android.content.Context
import androidx.work.WorkManager
import com.roozbehzarei.filester.upload.UploadManager
import com.roozbehzarei.filester.upload.UploadManagerImpl
import com.roozbehzarei.filester.upload.UploadNotificationFactory
import com.roozbehzarei.filester.upload.UploadWorker
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.worker

private fun provideWorkManager(context: Context) = WorkManager.getInstance(context)

val workerModule =
    module {
        single<UploadNotificationFactory>()
        single<UploadManagerImpl>() bind UploadManager::class
        single { create(::provideWorkManager) }
        worker<UploadWorker>()
    }
