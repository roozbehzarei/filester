package com.roozbehzarei.filester.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(file: FileEntity)

    @Query("SELECT * FROM fileentity")
    fun getAll(): Flow<List<FileEntity>>

    @Delete
    suspend fun delete(file: FileEntity)

}