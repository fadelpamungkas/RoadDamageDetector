package com.example.roaddamagedetector.data.remote

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @field:SerializedName("features")
    val features: List<PlaceItem>
)