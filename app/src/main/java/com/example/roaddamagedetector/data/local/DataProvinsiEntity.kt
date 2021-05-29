package com.example.roaddamagedetector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "dataprovinsi", primaryKeys = ["id"])
data class DataProvinsiEntity (

    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String
)