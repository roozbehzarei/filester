package com.roozbehzarei.filester.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FileEntity::class], version = 3, exportSchema = true)
abstract class FileDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `FileEntity` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `name` TEXT NOT NULL,
                            `url` TEXT NOT NULL,
                            `size` INTEGER NOT NULL,
                            `mimeType` TEXT
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        INSERT INTO `FileEntity` (`id`, `name`, `url`, `size`, `mimeType`)
                        SELECT `id`, `file_name`, `file_url`, `file_size`, NULL
                        FROM `File`
                        """.trimIndent(),
                    )
                    db.execSQL("DROP TABLE IF EXISTS `File`")
                }
            }

        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE FileEntity ADD COLUMN uploadedAt INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE FileEntity ADD COLUMN expiresAt INTEGER NOT NULL DEFAULT 0")
                }
            }
    }
}
