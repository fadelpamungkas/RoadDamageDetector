package com.example.roaddamagedetector.data.remote

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class RoadDataResponse(

    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("photo")
    val photo: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("note")
    val note: String
)
