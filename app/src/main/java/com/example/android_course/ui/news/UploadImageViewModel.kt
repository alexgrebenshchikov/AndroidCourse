package com.example.android_course.ui.news

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.android_course.repository.UploadRepository
import com.example.android_course.repository.UploadRepositoryImp
import com.example.android_course.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class UploadImageViewModel(    private val uploadRepository: UploadRepository
) : BaseViewModel() {
    private val _viewState = MutableStateFlow<ViewState>(ViewState.Pending)
    val viewState: StateFlow<ViewState> = _viewState
    var imageIsLoaded = false
    var imageLink = ""


    fun uploadImage(uri : Uri) {
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading)
            val uploadResult = uploadRepository.uploadFile(uri)
            when(uploadResult) {
                is UploadRepositoryImp.UploadResult.Success -> {
                    Timber.d(uploadResult.uploadResult.toString())
                    imageIsLoaded = true
                    imageLink = uploadResult.uploadResult.link
                    _viewState.emit(ViewState.Success(uploadResult.uploadResult.link))
                }
                is UploadRepositoryImp.UploadResult.Error -> {
                    Timber.d(uploadResult.msg.toString())
                    _viewState.emit(ViewState.Error(uploadResult.msg))
                }
            }
        }
    }


    sealed class ViewState {
        object Pending : ViewState()
        object Loading : ViewState()
        data class Success(val link : String) : ViewState()
        data class Error(val msg: String?) : ViewState()
    }
}