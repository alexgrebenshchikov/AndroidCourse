package com.example.android_course.ui.signup

import androidx.lifecycle.viewModelScope
import com.example.android_course.repository.AuthRepository
import com.example.android_course.ui.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignUpViewModel : BaseViewModel() {
    private val _eventChannel = Channel<Event>(BUFFERED)

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
            try {
                /*AuthRepository.signUp(
                    firstname,
                    lastname,
                    nickname,
                    email,
                    password
                )*/
                // _eventChannel.send(Event.SignUpSuccess)
                _eventChannel.send(Event.SignUpEmailConfirmationRequired)
            } catch (error: Exception) {
                _eventChannel.send(Event.SignUpEmailConfirmationRequired)
            }
        }
    }

    sealed class Event {
        object SignUpSuccess : Event()
        object SignUpEmailConfirmationRequired : Event()
    }
}