package com.example.android_course.ui.userlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_course.data.network.Api
import com.example.android_course.ui.base.BaseViewModel
import com.example.android_course.util.User
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UserListViewModel : BaseViewModel() {
    companion object {
        val log_tag = "MainActivityLogTag"
    }
    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading)
    val viewState: StateFlow<ViewState> = _viewState

    init {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            delay(2000)
            val users = loadUsers()
            _viewState.value = ViewState.Data(users)
        }
    }

    sealed class ViewState {
        object Loading : ViewState()
        data class Data(val userList : List<User>) : ViewState()
    }

    private suspend fun loadUsers() : List<User> {
        Log.d(log_tag, "loadusers")
        return provideApi().getUsers().data
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


    private fun provideMoshi() : Moshi {
        return Moshi.Builder().build()
    }
}