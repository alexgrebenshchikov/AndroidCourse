package com.example.android_course.data.network.response.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyError(
    @Json(name = "code") val code : String,
    @Json(name = "message") val message : String
)