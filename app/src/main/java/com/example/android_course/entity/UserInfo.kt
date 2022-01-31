package com.example.android_course.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfo (
    @Json(name = "id") val id : String,
    @Json(name = "user_name") val userName : String,
    @Json(name = "first_name") val firstName : String,
    @Json(name = "last_name") val lastName : String,
    @Json(name = "picture") val picture : String?,
    @Json(name = "about_me") val aboutMe : String?,
    @Json(name = "email") val email : String,
    @Json(name = "phone_number") val phoneNumber : String?,
    @Json(name = "password") val password : String,
    @Json(name = "created_at") val createdAt : String,
    @Json(name = "updated_at") val updatedAt : String
)