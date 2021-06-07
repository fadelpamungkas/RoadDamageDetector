package com.example.roaddamagedetector.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeViewModel: ViewModel() {
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storageReference = FirebaseStorage.getInstance().getReference()

    fun getAllData() : LiveData<List<RoadDataEntity>> {
        val listData = MutableLiveData<List<RoadDataEntity>>()
        firestore.collection("roadDatabase")
            .addSnapshotListener{listener, exception ->
                if (listener != null) {
                    val list = ArrayList<RoadDataEntity>()
                    for (documentChange in listener.documentChanges) {
                        Log.d("DocumentChange","${documentChange.document.id}=>${documentChange.document.data}")
                        val data = RoadDataEntity(
                            Integer.parseInt(documentChange.document.data["id"].toString()),
                            documentChange.document.data["username"].toString(),
                            documentChange.document.data["email"].toString(),
                            documentChange.document.data["photo"].toString(),
                            documentChange.document.data["date"].toString(),
                            documentChange.document.data["address"].toString(),
                            documentChange.document.data["city"].toString(),
                            documentChange.document.data["note"].toString()
                        )
                        Log.d("DocumentChange", data.toString())
                        list.add(data)
                    }
                    listData.postValue(list)
                }

            }

        return listData
    }
}