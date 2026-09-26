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
import androidx.room.Room
import com.roozbehzarei.filester.data.local.FileDatabase
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create

private fun createFileDatabase(context: Context) =
    Room
        .databaseBuilder(context, FileDatabase::class.java, "FILE_DATABASE")
        .addMigrations(
            FileDatabase.MIGRATION_1_2,
            FileDatabase.MIGRATION_2_3,
        ).build()

private fun createFileDao(database: FileDatabase) = database.fileDao()

val databaseModule =
    module {
        single { create(::createFileDatabase) }
        single { create(::createFileDao) }
    }
