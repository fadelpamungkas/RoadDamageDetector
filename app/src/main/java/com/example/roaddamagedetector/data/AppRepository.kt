package com.example.roaddamagedetector.data

import com.example.roaddamagedetector.data.local.LocalSource
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.remote.ApiResponse
import com.example.roaddamagedetector.data.remote.PlaceItem
import com.example.roaddamagedetector.data.remote.RemoteSource
import com.example.roaddamagedetector.data.remote.RoadDataResponse
import com.example.roaddamagedetector.utils.AppExecutors
import com.example.roaddamagedetector.utils.DataMapper
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource,
    private val appExecutors: AppExecutors
) {

    companion object{
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource, appExecutors: AppExecutors): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(remoteSource, localSource, appExecutors).apply { instance = this }
            }
    }

    suspend fun getPlace(place: String): Flow<List<PlaceItem>> = remoteSource.getPlace(place)

    fun getRoadData(): Flow<Resource<List<RoadDataEntity>>> =
        object : NetworkBoundResource<List<RoadDataEntity>, List<RoadDataResponse>>() {
            override fun loadFromDB(): Flow<List<RoadDataEntity>> =
                localSource.getAllRoadData()

            override fun shouldFetch(data: List<RoadDataEntity>?): Boolean =
                data == null

            override suspend fun createCall(): Flow<ApiResponse<List<RoadDataResponse>>> {
                TODO("Not yet implemented")
            }

            override suspend fun saveCallResult(data: List<RoadDataResponse>) {
                localSource.insertList(
                    DataMapper.mapResponseToEntitiesList(data)
                )
            }

        }.asFlow()

    fun insertSingleData(data: RoadDataEntity) {
        appExecutors.diskIO().execute {
            localSource.insert(data)
        }
    }

}