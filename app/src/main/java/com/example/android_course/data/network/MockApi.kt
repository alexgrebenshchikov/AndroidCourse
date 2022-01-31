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
import java.lang.Error

class MockApi : Api {

    override suspend fun getUsers(): NetworkResponse<List<User>, VerificationErrorResponse> {
        return NetworkResponse.ServerError(VerificationErrorResponse(emptyList()), code = 400)
    }

    override suspend fun getProfile(): UserInfo {
        return UserInfo("42", "pumpkineater69", "Peter", "Griffin",
            null, null, "pg@gmail.com", null, "", "", "")
    }

    override suspend fun signInWithEmail(request: SignInWithEmailRequest): NetworkResponse<AuthTokens, SignInWithEmailErrorResponse> {
        return NetworkResponse.Success(
            AuthTokens(
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2MzgsImV4cCI6MTY0MDg3MTc3MX0.gzSraSYS8EXBxLN_oWnFSRgCzcmJmMjLiuyu5CSpyHI",
                refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2MzgsImV4cCI6MTY0MDg3MTc3MX0.gzSraSYS8EXBxLN_oWnFSRgCzcmJmMjLiuyu5CSpyHI",
                accessTokenExpiration = 1640871771000,
                refreshTokenExpiration = 1640871771000,
            ),
            code = 200
        )
        /*return NetworkResponse.ServerError(
            SignInWithEmailErrorResponse(
                email = listOf(Error("Invalid email!")),
                password = listOf(Error("Invalid password")),
                nonFieldErrors = listOf(Error("jojo"))
            ),
            code = 400
        )*/
    }

    override suspend fun refreshAuthTokens(request: RefreshAuthTokensRequest): NetworkResponse<AuthTokens, RefreshAuthTokensErrorResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun sendRegistrationVerificationCode(email: String): NetworkResponse<Unit, SendRegistrationVerificationCodeErrorResponse> {
        return NetworkResponse.Success(
            Unit,
            code = 200
        )
    }

    override suspend fun verifyRegistrationCode(
        code: String,
        email: String?,
        phoneNumber: String?
    ): NetworkResponse<VerificationTokenResponse, VerifyRegistrationCodeErrorResponse> {
        return when (code) {
            "420993" -> NetworkResponse.Success(
                VerificationTokenResponse(
                    verificationToken = "hfsdsjkhfkdsjhfkjdshfkjsdks"
                ),
                code = 200
            )
            else -> NetworkResponse.ServerError(
                VerifyRegistrationCodeErrorResponse(
                    nonFieldErrors = listOf(Error("Wrong verification code!"))
                ),
                code = 400
            )
        }
    }


    override suspend fun createProfile(request: CreateProfileRequest): NetworkResponse<UserInfo, CreateProfileErrorResponse> {
        /*return when (request.verificationToken) {
            "420993" -> NetworkResponse.Success(
                AuthTokens(
                    accessToken = "fyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2MzgsImV4cCI6MTY0MDg3MTc3MX0.gzSraSYS8EXBxLN_oWnFSRgCzcmJmMjLiuyu5CSpyHI",
                    refreshToken = "fyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2MzgsImV4cCI6MTY0MDg3MTc3MX0.gzSraSYS8EXBxLN_oWnFSRgCzcmJmMjLiuyu5CSpyHI",
                    accessTokenExpiration = 1640871771000,
                    refreshTokenExpiration = 1640871771000,
                ),
                code = 200
            )

            else -> NetworkResponse.ServerError(
                CreateProfileErrorResponse(
                    null,
                    /*listOf(Error("Wrong verification token!")),
                    listOf(Error("Invalid first name!")),
                    listOf(Error("Invalid last name!")),
                    listOf(Error("Invalid email!"), Error("waka waka")),
                    listOf(Error("Invalid password"))*/
                    null,
                    null,
                    null,
                    null,
                    null
                ),
                code = 400
            )
        }*/
        return NetworkResponse.Success(
            UserInfo("", "", "", "", "", "", "", "", "", "", ""),
            code = 200
        )

    }

    override suspend fun loadPosts(date : String, size : String): PostResponse {
        return PostResponse(emptyList(), false)
    }

    override suspend fun createPost(request: CreatePostRequest): Post {
        return Post(1, "", "", "", "", "", "")
    }

}