package com.example.roaddamagedetector.data.remote

import com.google.gson.annotations.SerializedName

class DataKabupatenResponse (

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)