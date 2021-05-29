package com.example.roaddamagedetector.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiRequest {
    @GET("data-indonesia/propinsi.json")
    suspend fun getProvinsi(): List<DataProvinsiResponse>

    @GET("data-indonesia/kabupaten/{id}.json")
    suspend fun getKabupaten(@Path("id") id: Int): List<DataKabupatenResponse>
}