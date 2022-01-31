package com.example.android_course.data.network.response

import com.example.android_course.entity.Post
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostResponse (
    @Json(name = "posts") val posts : List<Post>,
    @Json(name = "next") val next : Boolean

)