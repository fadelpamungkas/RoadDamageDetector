package com.example.roaddamagedetector.ui

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.roaddamagedetector.BuildConfig
import com.example.roaddamagedetector.data.AppRepository
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.data.remote.ApiConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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
//    private var _roads : MutableLiveData<ArrayList<RoadDataEntity>> = MutableLiveData<ArrayList<RoadDataEntity>>()
//
//
//    init {
//        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
//        listenToRoads()
//    }

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

//    private fun listenToRoads() {
//        firestore.collection("roadDatabase").addSnapshotListener {
//                snapshot, e ->
//            // if there is an exception we want to skip.
//            if (e != null) {
//                Log.w(ContentValues.TAG, "Listen Failed", e)
//                return@addSnapshotListener
//            }
//            // if we are here, we did not encounter an exception
//            if (snapshot != null) {
//                // now, we have a populated shapshot
//                val allRoads = ArrayList<RoadDataEntity>()
//                val documents = snapshot.documents
//                documents.forEach {
//
//                    val road = it.toObject(RoadDataEntity::class.java)
//                    if (road != null) {
//                        road.id = it.id.toInt()
//                        allRoads.add(road)
//                    }
//                }
//                _roads.value = allRoads
//            }
//        }
//    }

    fun save(
        road: RoadDataEntity
    ) {
        val document =
            if (!road.id.toString().isEmpty()) {
                // updating existing
                firestore.collection("roadDatabase").document(road.id.toString())
            } else {
                // create new
                firestore.collection("roadDatabase").document()
            }
        val set = document.set(road)
        set.addOnSuccessListener {
            Log.d("Firebase", "document saved")
                uploadPhotos(road)
        }
        set.addOnFailureListener {
            Log.d("Firebase", "Save Failed")
        }
    }

    private fun uploadPhotos(road: RoadDataEntity) {
        road.photo.forEach {
            val uri = Uri.parse(road.photo)
            val imageRef = storageReference.child("images/"  + "/" + uri.lastPathSegment)
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                val downloadUrl = imageRef.downloadUrl
                downloadUrl.addOnSuccessListener {
                    road.photo = it.toString()
                    // update our Cloud Firestore with the public image URI.
                    updatePhotoDatabase(road)
                }

            }
            uploadTask.addOnFailureListener {
                it.message?.let { it1 -> Log.e(ContentValues.TAG, it1) }
            }
        }
    }

    private fun updatePhotoDatabase(road: RoadDataEntity) {
        firestore.collection("roadDatabase")
            .document(road.id.toString())
            .set(road)
    }
}