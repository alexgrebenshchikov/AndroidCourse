package com.example.android_course.ui.onboarding

import com.example.android_course.ui.base.BaseViewModel

class OnBoardingViewModel : BaseViewModel() {
    var soundEnabled: Boolean = true
    var currentVolume: Float? = null
    var position : Long? = null
}