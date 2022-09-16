package com.roozbehzarei.filester.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(file: File)

    @Query("SELECT * FROM file")
    fun getAll(): Flow<List<File>>

}