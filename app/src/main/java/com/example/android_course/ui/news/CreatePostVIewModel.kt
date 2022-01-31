package com.example.android_course.ui.news

import androidx.lifecycle.viewModelScope
import com.example.android_course.data.network.Api
import com.example.android_course.data.network.request.CreatePostRequest
import com.example.android_course.entity.UserInfo
import com.example.android_course.interactor.AuthInteractor
import com.example.android_course.repository.UploadRepository
import com.example.android_course.ui.base.BaseViewModel
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
class CreatePostViewModel @Inject constructor(
    private val apiLazy: Lazy<Api>
) : BaseViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Pending)
    val viewState: StateFlow<ViewState> = _viewState
    private val api by lazy { apiLazy.get() }
    var isLoadingNeeded = true


    fun createPost(title : String, text : String, image : String, link : String) {
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading)
            try {
                val post = api.createPost(CreatePostRequest(title, text, image, link))
                _viewState.emit(ViewState.Success)
            } catch (e: Throwable) {
                Timber.d(e)
                _viewState.emit(ViewState.Error(e.message))
            }
        }
    }


    sealed class ViewState {
        object Pending : ViewState()
        object Loading : ViewState()
        object Success : ViewState()
        data class Error(val msg: String?) : ViewState()
    }
}