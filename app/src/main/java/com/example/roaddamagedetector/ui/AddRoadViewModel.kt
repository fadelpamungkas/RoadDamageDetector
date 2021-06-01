package com.example.roaddamagedetector.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.roaddamagedetector.BuildConfig
import com.example.roaddamagedetector.data.AppRepository
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.remote.ApiConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class AddRoadViewModel(private val appRepository: AppRepository) : ViewModel() {
    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    val searchResult = queryChannel.asFlow()
        .debounce(300)
        .distinctUntilChanged()
        .filter {
            it.trim().isNotEmpty()
        }
        .mapLatest {
            ApiConfig.getApiService().getPlace(it, BuildConfig.API_KEY).features
        }
        .asLiveData()

    fun insertSingleData(data: RoadDataEntity) = appRepository.insertSingleData(data)
}