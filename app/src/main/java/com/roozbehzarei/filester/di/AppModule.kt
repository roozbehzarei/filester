package com.roozbehzarei.filester.di

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.room.Room
import com.roozbehzarei.filester.data.local.FileDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.roozbehzarei.filester")
class AppModule

@Single
fun provideFileDatabase(applicationContext: Context): FileDatabase {
    val db = Room.databaseBuilder(applicationContext, FileDatabase::class.java, "FILE_DATABASE")
        .fallbackToDestructiveMigration(true).build()
    return db
}

@Single
fun provideFileDao(db: FileDatabase) = db.fileDao()

@Single
fun provideSnackbarHostState() = SnackbarHostState()