package com.example.android_course.ui.userlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_course.data.network.Api
import com.example.android_course.data.network.response.error.SignInWithEmailErrorResponse
import com.example.android_course.data.network.response.error.VerificationErrorResponse
import com.example.android_course.interactor.AuthInteractor
import com.example.android_course.ui.base.BaseViewModel
import com.example.android_course.ui.profile.ProfileViewModel
import com.example.android_course.ui.signin.SignInViewModel
import com.example.android_course.util.User
import com.haroldadmin.cnradapter.NetworkResponse
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class UserListViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val apiLazy: Lazy<Api>
) : BaseViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Pending)
    val viewState: StateFlow<ViewState> = _viewState
    private val _signInState = MutableStateFlow<ViewState>(ViewState.Pending)
    val signInState: StateFlow<ViewState> = _signInState

    private val api by lazy { apiLazy.get() }
    var tryAgain = true
    var isAuthorizationFailed = false
    var isLoadingNeeded = true


    sealed class ViewState {
        object Pending : ViewState()
        object Loading : ViewState()
        data class ServerError(val e: NetworkResponse.ServerError<VerificationErrorResponse>) :
            ViewState()

        data class NetworkError(val e: NetworkResponse.NetworkError) : ViewState()
        data class UnknownError(val e: NetworkResponse.UnknownError) : ViewState()
        data class Data(val userList: List<User>) : ViewState()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authInteractor.logout()
            } catch (error: Throwable) {
                Timber.e(error)
                //_eventChannel.send(ProfileViewModel.Event.LogoutError(error))
            }
        }
    }

    fun resetViewState() {
        viewModelScope.launch {
            _viewState.emit(ViewState.Pending)
        }
    }

    fun resetSignInState() {
        viewModelScope.launch {
            _signInState.emit(ViewState.Pending)
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading)
            try {
                val response = api.getUsers()
                Timber.d(response.toString())
                when (response) {

                    is NetworkResponse.Success -> {
                        Timber.d(response.body.toString())
                        _viewState.value = ViewState.Data(response.body)
                    }
                    is NetworkResponse.ServerError<*> -> {
                        Timber.d(response.body.toString())
                        if (!tryAgain)
                            _signInState.emit(ViewState.ServerError(response as NetworkResponse.ServerError<VerificationErrorResponse>))
                        _viewState.emit(ViewState.ServerError(response as NetworkResponse.ServerError<VerificationErrorResponse>))
                    }
                    is NetworkResponse.NetworkError -> {
                        Timber.d("network er")
                        _viewState.emit(ViewState.NetworkError(response))
                    }
                    is NetworkResponse.UnknownError -> {
                        Timber.d("unknown")
                        _viewState.emit(ViewState.UnknownError(response))
                    }
                    else -> {
                        Timber.d("unknown")
                    }
                }
            } catch (e: Throwable) {
                Timber.d(e)
                _viewState.emit(ViewState.UnknownError(NetworkResponse.UnknownError(e)))
            }
        }
    }

    private fun provideApi(): Api {
        return Retrofit.Builder()
            .client(provideOkHttpClient())
            .baseUrl("https://reqres.in/api/")
            .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
            .build()
            .create(Api::class.java)
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }


    private fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }
}