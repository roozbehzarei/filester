package com.roozbehzarei.filester.di

import android.content.Context
import androidx.room.Room
import com.roozbehzarei.filester.data.local.FileDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.roozbehzarei.filester.data.local")
class DatabaseModule

@Single
fun provideFileDatabase(applicationContext: Context): FileDatabase {
    val db = Room.databaseBuilder(applicationContext, FileDatabase::class.java, "FILE_DATABASE")
        .fallbackToDestructiveMigration(true).build()
    return db
}

@Single
fun provideFileDao(db: FileDatabase) = db.fileDao()