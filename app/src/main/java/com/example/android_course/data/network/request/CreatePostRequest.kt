package com.example.android_course.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePostRequest(
    @Json(name = "title") val title: String,
    @Json(name = "text") val text: String,
    @Json(name = "image") val image: String,
    @Json(name = "link") val link: String,
)
