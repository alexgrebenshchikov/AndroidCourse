package com.example.android_course.ui.news

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android_course.data.network.Api
import com.example.android_course.entity.Post
import com.example.android_course.entity.UserInfo
import com.example.android_course.interactor.AuthInteractor
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
class NewsViewModel @Inject constructor(
    private val apiLazy: Lazy<Api>

): BaseViewModel() {

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading)
    val viewState: StateFlow<ViewState> = _viewState
    private val api by lazy { apiLazy.get() }
    var isLoadingNeeded = true
    val postsFlow: Flow<PagingData<Post>> =
        Pager(config = PagingConfig(pageSize = 20, prefetchDistance = 2),
            pagingSourceFactory = { PostPagingDataSource(api) }
        ).flow.cachedIn(viewModelScope)



    fun loadPosts() {
        viewModelScope.launch {
            _viewState.emit(ViewState.Loading)
            try{
                val posts = api.loadPosts("2021-11-17T13:34:55.718Z", "1").posts
                Timber.d(posts.toString())
                _viewState.emit(ViewState.Data(posts))
            } catch(e : Throwable) {
                Timber.e(e)
            }
        }
    }







    sealed class ViewState {
        object Loading : ViewState()
        data class Data(val posts : List<Post>) : ViewState()
    }
}