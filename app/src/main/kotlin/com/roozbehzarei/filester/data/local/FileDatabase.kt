package com.roozbehzarei.filester.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FileEntity::class], version = 3, exportSchema = true)
abstract class FileDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE FileEntity ADD COLUMN uploadedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE FileEntity ADD COLUMN expiresAt INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

}