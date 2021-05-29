package com.example.roaddamagedetector.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertKabupaten(data: List<DataKabupatenEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProvinsi(data: List<DataProvinsiEntity>)

    @Query("SELECT * FROM dataprovinsi")
    fun getProvinsi(): Flow<List<DataProvinsiEntity>>

    @Query("SELECT * FROM datakabupaten WHERE id = :id")
    fun getKabupaten(id: Int): Flow<List<DataKabupatenEntity>>

}