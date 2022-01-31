package com.example.android_course.data.network.response.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInWithEmailErrorResponse(
    @Json(name = "non_field_errors") val nonFieldErrors: List<MyError>?,
    @Json(name = "email") val email: List<MyError>?,
    @Json(name = "password") val password: List<MyError>?
)