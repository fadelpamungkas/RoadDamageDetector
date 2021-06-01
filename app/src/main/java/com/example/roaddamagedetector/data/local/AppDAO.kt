package com.example.roaddamagedetector.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: RoadDataEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(data: List<RoadDataEntity>)

    @Delete
    fun delete(data: RoadDataEntity)

    @Query("SELECT * FROM roaddata")
    fun getAllRoadData(): Flow<List<RoadDataEntity>>

    @Query("SELECT * FROM roaddata WHERE id = :id")
    fun getRoadDataById(id: Int): Flow<RoadDataEntity>

}