package com.roozbehzarei.filester.di

import android.content.Context
import androidx.room.Room
import com.roozbehzarei.filester.data.local.FileDatabase
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create

private fun createFileDatabase(context: Context) =
    Room.databaseBuilder(context, FileDatabase::class.java, "FILE_DATABASE")
        .fallbackToDestructiveMigration(true).build()

private fun createFileDao(database: FileDatabase) = database.fileDao()

val databaseModule = module {
    single { create(::createFileDatabase) }
    single { create(::createFileDao) }
}