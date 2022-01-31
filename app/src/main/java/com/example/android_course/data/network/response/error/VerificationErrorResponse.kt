package com.example.android_course.data.network.response.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerificationErrorResponse(
    @Json(name = "verification_error") val verificationError: List<MyError>?
)