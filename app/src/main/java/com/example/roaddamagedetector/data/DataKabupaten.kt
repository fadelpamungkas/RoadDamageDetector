package com.example.roaddamagedetector.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataKabupaten (

    val id: String,
    val name: String

): Parcelable