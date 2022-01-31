package com.example.android_course.data.network

import com.example.android_course.data.network.request.CreatePostRequest
import com.example.android_course.data.network.request.CreateProfileRequest
import com.example.android_course.data.network.request.RefreshAuthTokensRequest
import com.example.android_course.data.network.request.SignInWithEmailRequest
import com.example.android_course.data.network.response.PostResponse
import com.example.android_course.data.network.response.VerificationTokenResponse
import com.example.android_course.data.network.response.error.*
import com.example.android_course.entity.AuthTokens
import com.example.android_course.entity.Post
import com.example.android_course.entity.UserInfo
import com.example.android_course.util.User
import com.haroldadmin.cnradapter.NetworkResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.*

/*interface Api {
    @GET("users")
    suspend fun getUsers() : getUsersResponse
}

@JsonClass(generateAdapter = true)
data class getUsersResponse(
    @Json(name = "data") val data: List<User>
)*/

interface Api {

    @GET("users?per_page=10")
    suspend fun getUsers(): NetworkResponse<List<User>, VerificationErrorResponse>

    @GET("users/get-profile")
    suspend fun getProfile(): UserInfo

    @POST("/auth/sign-in-with-email")
    suspend fun signInWithEmail(
        @Body request: SignInWithEmailRequest
    ): NetworkResponse<AuthTokens, SignInWithEmailErrorResponse>

    @POST("auth/refresh-tokens")
    suspend fun refreshAuthTokens(
        @Body request: RefreshAuthTokensRequest
    ): NetworkResponse<AuthTokens, RefreshAuthTokensErrorResponse>

    @POST("registration/verification-code/send")
    suspend fun sendRegistrationVerificationCode(
        @Query("email") email: String,
    ): NetworkResponse<Unit, SendRegistrationVerificationCodeErrorResponse>

    @POST("registration/verification-code/verify")
    suspend fun verifyRegistrationCode(
        @Query("code") code: String,
        @Query("email") email: String?,
        @Query("phone_number") phoneNumber: String?
    ): NetworkResponse<VerificationTokenResponse, VerifyRegistrationCodeErrorResponse>

    @POST("registration/create-profile")
    suspend fun createProfile(
        @Body request: CreateProfileRequest
    ): NetworkResponse<UserInfo, CreateProfileErrorResponse>


    @GET("posts")
    suspend fun loadPosts(
        @Query("from") date: String,
        @Query("pageSize") size: String
    ): PostResponse

    @POST("posts")
    suspend fun createPost(
        @Body request: CreatePostRequest
    ) : Post

    /*@POST("/registration/create-profile")
    suspend fun createProfileAlt(
        @Body request: CreateProfileAltRequest
    ) : NetworkResponse<UserInfo, CreateProfileAltErrorResponse>*/
}

@JsonClass(generateAdapter = true)
data class GetUsersResponse(
    @Json(name = "data") val data: List<User>
)