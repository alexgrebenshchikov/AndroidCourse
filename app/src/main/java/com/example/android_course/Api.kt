package com.example.android_course

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET

interface Api {
    @GET("users")
    suspend fun getUsers() : getUsersResponse
}

@JsonClass(generateAdapter = true)
data class getUsersResponse(
    @Json(name = "data") val data: List<User>
)