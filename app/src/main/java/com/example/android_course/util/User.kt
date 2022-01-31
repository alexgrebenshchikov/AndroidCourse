package com.example.android_course.util

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "id") val id : String,
    @Json(name = "user_name")val userName : String,
    @Json(name = "first_name") val firstName : String,
    @Json(name = "last_name") val lastName : String,
    @Json(name = "picture") val picture : String?,
    @Json(name = "about_me")val aboutMe : String?
)