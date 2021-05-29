package com.example.roaddamagedetector.data

import com.example.roaddamagedetector.data.local.LocalSource
import com.example.roaddamagedetector.data.remote.ApiResponse
import com.example.roaddamagedetector.data.remote.DataKabupatenResponse
import com.example.roaddamagedetector.data.remote.DataProvinsiResponse
import com.example.roaddamagedetector.data.remote.RemoteSource
import com.example.roaddamagedetector.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) {

    companion object{
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(remoteSource, localSource).apply { instance = this }
            }
    }

    fun getProvinsi(): Flow<Resource<List<DataProvinsi>>> =
        object: NetworkBoundResource<List<DataProvinsi>, List<DataProvinsiResponse>>() {
            override fun loadFromDB(): Flow<List<DataProvinsi>> {
                return localSource.getProvinsi().map {
                    DataMapper.mapEntitiesToModelProvinsi(it)
                }
            }

            override fun shouldFetch(data: List<DataProvinsi>?): Boolean =
                true

            override suspend fun createCall(): Flow<ApiResponse<List<DataProvinsiResponse>>> =
                remoteSource.getProvinsi()

            override suspend fun saveCallResult(data: List<DataProvinsiResponse>) {
                localSource.insertProvinsi(DataMapper.mapResponsesToEntitiesProvinsi(data))
            }

        }.asFlow()

    fun getKabupaten(id: Int): Flow<Resource<List<DataKabupaten>>> =
        object: NetworkBoundResource<List<DataKabupaten>, List<DataKabupatenResponse>>() {
            override fun loadFromDB(): Flow<List<DataKabupaten>> {
                return localSource.getKabupaten(id).map {
                    DataMapper.mapEntitiesToModelKabupaten(it)
                }
            }

            override fun shouldFetch(data: List<DataKabupaten>?): Boolean =
                true

            override suspend fun createCall(): Flow<ApiResponse<List<DataKabupatenResponse>>> =
                remoteSource.getKabupaten(id)

            override suspend fun saveCallResult(data: List<DataKabupatenResponse>) {
                localSource.insertKabupaten(DataMapper.mapResponsesToEntitiesKabupaten(data))
            }

        }.asFlow()
}