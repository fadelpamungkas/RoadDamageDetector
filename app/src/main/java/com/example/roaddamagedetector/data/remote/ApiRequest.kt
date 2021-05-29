package com.example.roaddamagedetector.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiRequest {

    @GET("mapbox.places/{place}.json")
    suspend fun getPlace(
        @Path("place") place: String,
        @Query("access_token") accessToken: String,
        @Query("autocomplete") autoComplete: Boolean = true,
        @Query("country") country: String = "id",
        @Query("types") type: String = "region,locality,place"
    ): PlaceResponse

}