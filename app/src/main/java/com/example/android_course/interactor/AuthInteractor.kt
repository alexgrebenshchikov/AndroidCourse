package com.example.android_course.interactor

import com.example.android_course.data.network.response.VerificationTokenResponse
import com.example.android_course.data.network.response.error.CreateProfileErrorResponse
import com.example.android_course.data.network.response.error.SendRegistrationVerificationCodeErrorResponse
import com.example.android_course.data.network.response.error.SignInWithEmailErrorResponse
import com.example.android_course.data.network.response.error.VerifyRegistrationCodeErrorResponse
import com.example.android_course.entity.AuthTokens
import com.example.android_course.entity.UserInfo
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
                //val wrong = AuthTokens("fdfdf", response.body.refreshToken, 12, 12)
                authRepository.saveAuthTokens(response.body)
                //authRepository.saveAuthTokens(wrong)
            }
            is NetworkResponse.Error -> {
                Timber.e(response.error)
            }
        }
        return response
    }

    suspend fun signUpWithEmailAnfPersonalInfo(
        userName: String,
        firstname: String,
        lastname: String,
        email: String,
        password: String,
        verificationCode: String
    ): NetworkResponse<UserInfo, CreateProfileErrorResponse> {
        val response = authRepository.generateAuthTokensByEmailAndPersonalInfo(
            userName, firstname, lastname, email, password
        )

        /*when (response) {
            is NetworkResponse.Success -> {
                authRepository.saveAuthTokens(response.body)
                //authRepository.saveUserInfo(UserInfo(firstname, lastname, nickname, email, password))
            }
            is NetworkResponse.Error -> {
                Timber.e(response.error)
            }
        }*/
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