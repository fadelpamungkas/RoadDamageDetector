package com.example.roaddamagedetector.data.remote

import com.google.gson.annotations.SerializedName

data class DataProvinsiResponse(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)
