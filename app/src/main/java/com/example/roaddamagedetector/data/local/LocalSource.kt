package com.example.roaddamagedetector.data.local

import kotlinx.coroutines.flow.Flow

class LocalSource private constructor(
    private val appDAO: AppDAO
){
    companion object {
        private var INSTANCE: LocalSource? = null

        fun getInstance(appDAO: AppDAO): LocalSource =
            INSTANCE ?: LocalSource(appDAO)
    }

    fun insert(data: RoadDataEntity) =
        appDAO.insert(data)

    fun insertList(data: List<RoadDataEntity>) =
        appDAO.insertList(data)

    fun delete(data: RoadDataEntity) =
        appDAO.delete(data)

    fun getAllRoadData(): Flow<List<RoadDataEntity>> =
        appDAO.getAllRoadData()

    fun getRoadDataById(id: Int): Flow<RoadDataEntity> =
        appDAO.getRoadDataById(id)
}