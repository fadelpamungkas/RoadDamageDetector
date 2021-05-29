package com.example.roaddamagedetector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "datakabupaten", primaryKeys = ["id"])
data class DataKabupatenEntity (

    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String

)