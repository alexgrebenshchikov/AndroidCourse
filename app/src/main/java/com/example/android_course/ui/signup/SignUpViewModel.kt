package com.example.android_course.ui.signup

import androidx.lifecycle.viewModelScope
import com.example.android_course.data.network.response.error.CreateProfileErrorResponse
import com.example.android_course.data.network.response.error.SendRegistrationVerificationCodeErrorResponse
import com.example.android_course.data.network.response.error.SignInWithEmailErrorResponse
import com.example.android_course.interactor.AuthInteractor
import com.example.android_course.repository.AuthRepository
import com.example.android_course.ui.base.BaseViewModel
import com.example.android_course.ui.signin.SignInViewModel
import com.haroldadmin.cnradapter.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
) : BaseViewModel() {
    private val _eventChannel = Channel<Event>(BUFFERED)

    private val _signUpActionStateFlow = MutableStateFlow<SignUpActionState>(
        SignUpActionState.Pending
    )

    private val _sendVerificationCodeStateFlow = MutableStateFlow<SendVerificationCodeActionState>(
        SendVerificationCodeActionState.Pending
    )

    fun signUpActionStateFlow(): Flow<SignUpActionState> {
        return _signUpActionStateFlow.asStateFlow()
    }

    fun sendVerificationCodeStateFlow(): Flow<SendVerificationCodeActionState> {
        return _sendVerificationCodeStateFlow.asStateFlow()
    }

    fun eventsFlow(): Flow<Event> {
        return _eventChannel.receiveAsFlow()
    }

    fun signUp(
        firstname: String,
        lastname: String,
        nickname: String,
        email: String,
        password: String
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



            /*_signUpActionStateFlow.emit(SignUpActionState.Loading)
            try {
                when (val response = authInteractor.signUpWithEmailAnfPersonalInfo(
                    firstname,
                    lastname, nickname, email, password, "420"
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
            }*/
        }
    }

    sealed class Event {
        object SignUpSuccess : Event()
        object SignUpEmailConfirmationRequired : Event()
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


    sealed class SignUpActionState {
        object Pending : SignUpActionState()
        object Loading : SignUpActionState()
        data class ServerError(val e: NetworkResponse.ServerError<CreateProfileErrorResponse>) :
            SignUpActionState()

        data class NetworkError(val e: NetworkResponse.NetworkError) : SignUpActionState()
        data class UnknownError(val e: NetworkResponse.UnknownError) : SignUpActionState()
    }
}
