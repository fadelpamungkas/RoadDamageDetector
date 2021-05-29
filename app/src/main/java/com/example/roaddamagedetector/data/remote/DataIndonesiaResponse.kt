package com.example.roaddamagedetector.data.remote

import com.google.gson.annotations.SerializedName

data class DataIndonesiaResponse <T>(

    @SerializedName("results")
    val result: List<T>
    )
