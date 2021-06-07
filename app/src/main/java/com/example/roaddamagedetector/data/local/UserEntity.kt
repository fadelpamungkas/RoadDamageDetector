package com.example.roaddamagedetector.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "user", primaryKeys = ["id"])
data class UserEntity(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "confirm_password")
    val confirm_password: String,

    @ColumnInfo(name = "photo")
    var photo: String

)