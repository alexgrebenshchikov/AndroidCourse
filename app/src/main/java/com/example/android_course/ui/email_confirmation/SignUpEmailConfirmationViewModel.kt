package com.example.android_course.ui.email_confirmation

import androidx.lifecycle.viewModelScope
import com.example.android_course.data.network.response.error.CreateProfileErrorResponse
import com.example.android_course.data.network.response.error.SendRegistrationVerificationCodeErrorResponse
import com.example.android_course.data.network.response.error.VerifyRegistrationCodeErrorResponse
import com.example.android_course.interactor.AuthInteractor
import com.example.android_course.ui.base.BaseViewModel
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpEmailConfirmationViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : BaseViewModel() {
    var email : String = ""
    var password : String = ""
    var firstname : String = ""
    var lastname : String = ""
    var nickname : String = ""
    var code : String = ""



    private val _signUpActionStateFlow = MutableStateFlow<SignUpActionState>(
        SignUpActionState.Pending
    )

    private val _verifyRegistrationCodeActionStateFlow = MutableStateFlow<VerifyRegistrationCodeActionState>(
        VerifyRegistrationCodeActionState.Pending
    )

    private val _sendVerificationCodeStateFlow = MutableStateFlow<SendVerificationCodeActionState>(
        SendVerificationCodeActionState.Pending
    )

    fun sendVerificationCodeStateFlow(): Flow<SendVerificationCodeActionState> {
        return _sendVerificationCodeStateFlow.asStateFlow()
    }

    fun signUpActionStateFlow(): Flow<SignUpActionState> {
        return _signUpActionStateFlow.asStateFlow()
    }

    fun verifyRegistrationCodeActionStateFlow() : Flow<VerifyRegistrationCodeActionState> {
        return _verifyRegistrationCodeActionStateFlow.asStateFlow()
    }

    fun resetSendVerificationCodeStateFlow() {
        viewModelScope.launch {
            _sendVerificationCodeStateFlow.emit(SendVerificationCodeActionState.Pending)
        }
    }

    fun resetVerifyRegistrationCodeActionStateFlow() {
        viewModelScope.launch {
            _verifyRegistrationCodeActionStateFlow.emit(VerifyRegistrationCodeActionState.Pending)
        }
    }

    fun resetSignUpActionStateFlow()  {
        viewModelScope.launch {
            _signUpActionStateFlow.emit(SignUpActionState.Pending)
        }
    }

    fun signUp(
        firstname: String,
        lastname: String,
        nickname: String,
        email: String,
        password: String,
        verificationCode : String
    ) {
        Timber.d("$firstname $lastname $nickname $email $password")
        viewModelScope.launch {
            _signUpActionStateFlow.emit(SignUpActionState.Loading)
            Timber.d("signup!")
            try {
                when (val response = authInteractor.signUpWithEmailAnfPersonalInfo(
                    firstname,
                    lastname, nickname, email, password, verificationCode
                )) {
                    is NetworkResponse.Success<*> -> {
                        Timber.d("success")
                        _signUpActionStateFlow.emit(SignUpActionState.Pending)
                    }
                    is NetworkResponse.ServerError<*> -> {
                        _signUpActionStateFlow.emit(
                            SignUpActionState.ServerError(
                                response as NetworkResponse.ServerError<CreateProfileErrorResponse>
                            )
                        )
                    }
                    is NetworkResponse.NetworkError -> {
                        _signUpActionStateFlow.emit(SignUpActionState.NetworkError(response))
                    }
                    is NetworkResponse.UnknownError -> {
                        _signUpActionStateFlow.emit(SignUpActionState.UnknownError(response))
                    }
                }
            } catch (error: Throwable) {
                Timber.e(error)
                _signUpActionStateFlow.emit(
                    SignUpActionState.UnknownError(
                        NetworkResponse.UnknownError(
                            error
                        )
                    )
                )
            }
        }
    }


    fun verifyRegistrationCode(code: String,
                               email: String?,
                               phoneNumber: String?) {
        viewModelScope.launch {
            _verifyRegistrationCodeActionStateFlow.emit(VerifyRegistrationCodeActionState.Loading)
            try {
                when (val response = authInteractor.verifyRegistrationCode(
                    code, email, phoneNumber
                )) {
                    is NetworkResponse.Success<*> -> {
                        Timber.d("success")
                        _verifyRegistrationCodeActionStateFlow.emit(VerifyRegistrationCodeActionState.Success)
                    }
                    is NetworkResponse.ServerError<*> -> {
                        _verifyRegistrationCodeActionStateFlow.emit(
                            VerifyRegistrationCodeActionState.ServerError(
                                response as NetworkResponse.ServerError<VerifyRegistrationCodeErrorResponse>
                            )
                        )
                    }
                    is NetworkResponse.NetworkError -> {
                        _verifyRegistrationCodeActionStateFlow.emit(VerifyRegistrationCodeActionState.NetworkError(response))
                    }
                    is NetworkResponse.UnknownError -> {
                        _verifyRegistrationCodeActionStateFlow.emit(VerifyRegistrationCodeActionState.UnknownError(response))
                    }
                }
            } catch (error: Throwable) {
                Timber.e(error)
                _verifyRegistrationCodeActionStateFlow.emit(
                    VerifyRegistrationCodeActionState.UnknownError(
                        NetworkResponse.UnknownError(
                            error
                        )
                    )
                )
            }
        }
    }

    fun sendCode(
        email: String,
    ) {
        viewModelScope.launch {
            _sendVerificationCodeStateFlow.emit(SendVerificationCodeActionState.Loading)
            try {
                when (val response = authInteractor.sendVerificationCode(email)) {
                    is NetworkResponse.Success<*> -> {
                        _sendVerificationCodeStateFlow.emit(SendVerificationCodeActionState.Success)
                    }
                    is NetworkResponse.ServerError<*> -> {
                        _sendVerificationCodeStateFlow.emit(
                            SendVerificationCodeActionState.ServerError(
                                response as NetworkResponse.ServerError<SendRegistrationVerificationCodeErrorResponse>
                            )
                        )
                    }
                    is NetworkResponse.NetworkError -> {
                        _sendVerificationCodeStateFlow.emit(SendVerificationCodeActionState.NetworkError(response))
                    }
                    is NetworkResponse.UnknownError -> {
                        _sendVerificationCodeStateFlow.emit(SendVerificationCodeActionState.UnknownError(response))
                    }

                }
            } catch (error: Throwable) {
                Timber.e(error)
                _sendVerificationCodeStateFlow.emit(
                    SendVerificationCodeActionState.UnknownError(
                        NetworkResponse.UnknownError(error)
                    )
                )
            }
        }
    }

    sealed class SignUpActionState {
        object Pending : SignUpActionState()
        object Loading : SignUpActionState()
        data class ServerError(val e: NetworkResponse.ServerError<CreateProfileErrorResponse>) :
            SignUpActionState()

        data class NetworkError(val e: NetworkResponse.NetworkError) : SignUpActionState()
        data class UnknownError(val e: NetworkResponse.UnknownError) : SignUpActionState()
    }

    sealed class VerifyRegistrationCodeActionState {
        object Success : VerifyRegistrationCodeActionState()
        object Pending : VerifyRegistrationCodeActionState()
        object Loading : VerifyRegistrationCodeActionState()
        data class ServerError(val e: NetworkResponse.ServerError<VerifyRegistrationCodeErrorResponse>) :
            VerifyRegistrationCodeActionState()

        data class NetworkError(val e: NetworkResponse.NetworkError) : VerifyRegistrationCodeActionState()
        data class UnknownError(val e: NetworkResponse.UnknownError) : VerifyRegistrationCodeActionState()
    }

    sealed class SendVerificationCodeActionState {
        object Success : SendVerificationCodeActionState()
        object Loading : SendVerificationCodeActionState()
        object Pending : SendVerificationCodeActionState()
        data class ServerError(
            val e: NetworkResponse.ServerError<SendRegistrationVerificationCodeErrorResponse>
        ) :
            SendVerificationCodeActionState()

        data class NetworkError(val e: NetworkResponse.NetworkError) :
            SendVerificationCodeActionState()

        data class UnknownError(val e: NetworkResponse.UnknownError) :
            SendVerificationCodeActionState()
    }



}