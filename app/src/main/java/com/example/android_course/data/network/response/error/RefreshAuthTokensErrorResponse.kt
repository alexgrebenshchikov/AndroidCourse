package com.example.android_course.data.network.response.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshAuthTokensErrorResponse(
    @Json(name = "refresh") val refresh: List<MyError>?
)