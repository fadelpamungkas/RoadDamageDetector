package com.example.roaddamagedetector.data

import com.example.roaddamagedetector.data.local.LocalSource
import com.example.roaddamagedetector.data.remote.PlaceItem
import com.example.roaddamagedetector.data.remote.RemoteSource
import kotlinx.coroutines.flow.Flow

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

    suspend fun getPlace(place: String): Flow<List<PlaceItem>> = remoteSource.getPlace(place)

}