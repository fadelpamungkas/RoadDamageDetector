package com.example.roaddamagedetector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "roaddata", primaryKeys = ["id"])
data class RoadDataEntity(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "photo")
    var photo: String,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "note")
    val note: String
)
