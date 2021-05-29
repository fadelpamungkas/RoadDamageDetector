package com.example.roaddamagedetector.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteSource() {

    companion object{
        @Volatile
        private var instance: RemoteSource? = null

        fun getInstance(): RemoteSource =
            instance ?: synchronized(this) {
                instance ?: RemoteSource().apply { instance = this }
            }
    }

    private var apiService = ApiConfig.getApiService()

    suspend fun getProvinsi(): Flow<ApiResponse<List<DataProvinsiResponse>>> {
        return flow {
            try {
                val response = apiService.getProvinsi()
                if (response.isNotEmpty()) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getKabupaten(id: Int): Flow<ApiResponse<List<DataKabupatenResponse>>> {
        return flow {
            try {
                val response = apiService.getKabupaten(id)
                if (response.isNotEmpty()) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
}