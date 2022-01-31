package com.example.android_course.data.network.response.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateProfileErrorResponse(
    @Json(name = "user_name") val userName: List<MyError>?,
    @Json(name = "email") val email: List<MyError>?,
)
