package com.example.roaddamagedetector.ui

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.roaddamagedetector.BuildConfig
import com.example.roaddamagedetector.data.AppRepository
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.remote.ApiConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class AddRoadViewModel(private val appRepository: AppRepository) : ViewModel() {
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storageReference = FirebaseStorage.getInstance().getReference()
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

    fun save(road: RoadDataEntity) {
        val uri = Uri.parse(road.photo)
        val imageRef = storageReference.child("images/"  + "/" + uri.lastPathSegment)
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val downloadUrl = imageRef.downloadUrl
            downloadUrl.addOnSuccessListener {
                road.photo = it.toString()
                // update our Cloud Firestore with the public image URI.
                firestore.collection("roadDatabase")
                    .document()
                    .set(road)
            }

        }
        uploadTask.addOnFailureListener {
            it.message?.let { it1 -> Log.e(ContentValues.TAG, it1) }
        }
    }
}