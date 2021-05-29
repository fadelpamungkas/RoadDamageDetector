package com.example.roaddamagedetector.data.remote

import android.util.Log
import com.example.roaddamagedetector.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteSource {

    companion object{
        @Volatile
        private var instance: RemoteSource? = null

        fun getInstance(): RemoteSource =
            instance ?: synchronized(this) {
                instance ?: RemoteSource().apply { instance = this }
            }
    }

    private var apiService = ApiConfig.getApiService()

    suspend fun getPlace(place: String): Flow<List<PlaceItem>> {
        return flow {
            try {
                val response = apiService.getPlace(place, BuildConfig.API_KEY).features
                emit(response)
            } catch (e: Exception) {
                Log.e("RemoteSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

}