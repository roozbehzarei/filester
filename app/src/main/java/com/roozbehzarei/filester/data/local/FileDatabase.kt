package com.roozbehzarei.filester.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [File::class], version = 2, exportSchema = false)
abstract class FileDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

}