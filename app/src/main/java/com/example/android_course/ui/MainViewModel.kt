package com.example.android_course.ui

import com.example.android_course.interactor.AuthInteractor
import com.example.android_course.repository.AuthRepository
import com.example.android_course.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authInteractor: AuthInteractor
    ) : BaseViewModel() {

   suspend fun isAuthorizedFlow() : Flow<Boolean> = authInteractor.isAuthorized()
}