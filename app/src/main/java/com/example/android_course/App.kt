package com.example.android_course

import android.app.Application
import com.example.android_course.di.module.appModule
import com.example.android_course.di.module.networkModule
import com.example.android_course.di.module.viewModelModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        startKoin {
            androidContext(this@App)
            modules(appModule, networkModule, viewModelModule)
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}