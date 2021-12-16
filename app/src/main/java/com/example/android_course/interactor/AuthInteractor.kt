package com.example.android_course.interactor

import com.example.android_course.data.network.response.VerificationTokenResponse
import com.example.android_course.data.network.response.error.CreateProfileErrorResponse
import com.example.android_course.data.network.response.error.SendRegistrationVerificationCodeErrorResponse
import com.example.android_course.data.network.response.error.SignInWithEmailErrorResponse
import com.example.android_course.data.network.response.error.VerifyRegistrationCodeErrorResponse
import com.example.android_course.entity.AuthTokens
import com.example.android_course.repository.AuthRepository
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun isAuthorized(): Flow<Boolean> =
        authRepository.isAuthorizedFlow()

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): NetworkResponse<AuthTokens, SignInWithEmailErrorResponse> {
        val response = authRepository.generateAuthTokensByEmail(email, password)
        when (response) {
            is NetworkResponse.Success -> {
                authRepository.saveAuthTokens(response.body)
            }
            is NetworkResponse.Error -> {
                Timber.e(response.error)
            }
        }
        return response
    }

    suspend fun signUpWithEmailAnfPersonalInfo(
        firstname: String,
        lastname: String,
        nickname: String,
        email: String,
        password: String,
        verificationCode: String
    ): NetworkResponse<AuthTokens, CreateProfileErrorResponse> {
        val response = authRepository.generateAuthTokensByEmailAndPersonalInfo(
            email,
            verificationCode, firstname, lastname, password
        )

        when (response) {
            is NetworkResponse.Success -> {
                authRepository.saveAuthTokens(response.body)
            }
            is NetworkResponse.Error -> {
                Timber.e(response.error)
            }
        }
        return response
    }

    suspend fun sendVerificationCode(email: String):
            NetworkResponse<Unit, SendRegistrationVerificationCodeErrorResponse> {
        return authRepository.sendVerificationCode(email)
    }

    suspend fun verifyRegistrationCode(code: String,
                                       email: String?,
                                       phoneNumber: String?):
            NetworkResponse<VerificationTokenResponse, VerifyRegistrationCodeErrorResponse> {
        return authRepository.verifyRegistrationCode(code, email, phoneNumber)
    }


    suspend fun logout() {
        authRepository.saveAuthTokens(null)
    }
}