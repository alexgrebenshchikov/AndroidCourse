package com.example.android_course.ui.profile

import androidx.lifecycle.viewModelScope
import com.example.android_course.data.network.Api
import com.example.android_course.entity.UserInfo
import com.example.android_course.interactor.AuthInteractor
import com.example.android_course.ui.base.BaseViewModel
import com.example.android_course.util.User
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val apiLazy: Lazy<Api>

): BaseViewModel() {

    private val _eventChannel = Channel<Event>(Channel.BUFFERED)
    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading)
    val viewState: StateFlow<ViewState> = _viewState
    private val api by lazy { apiLazy.get() }
    var isLoadingNeeded = true



    fun loadUserProfile() {
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading)
            val userProfile = api.getProfile()
            _viewState.emit(ViewState.Data(userProfile))
        }
    }


    fun eventsFlow(): Flow<Event> {
        return _eventChannel.receiveAsFlow()
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authInteractor.logout()
            } catch (error: Throwable) {
                Timber.e(error)
                _eventChannel.send(Event.LogoutError(error))
            }
        }
    }

    sealed class Event {
        data class LogoutError(val error: Throwable) : Event()
    }

    sealed class ViewState {
        object Loading : ViewState()
        data class Data(val userProfile : UserInfo) : ViewState()
    }
}