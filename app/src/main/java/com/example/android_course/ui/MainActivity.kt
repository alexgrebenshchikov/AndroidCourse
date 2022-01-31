package com.example.android_course.ui

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.android_course.R
import com.example.android_course.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToAuthorizationStatus()
    }

    private fun subscribeToAuthorizationStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isAuthorizedFlow().collect {
                    showSuitableNavigationFlow(it)
                }
            }
        }
    }

    // This method have to be idempotent. Do not override restored backstack.
    private fun showSuitableNavigationFlow(isAuthorized: Boolean) {
        val navController = findNavController(R.id.mainActivityNavigationHost)
        when (isAuthorized) {
            true -> {
                if (navController.backQueue.any { it.destination.id == R.id.registered_user_nav_graph}) {
                    return
                }
                navController.navigate(R.id.action_registeredUserNavGraph)
            }
            false -> {
                if (navController.backQueue.any { it.destination.id == R.id.guest_nav_graph}) {
                    return
                }
                navController.navigate(R.id.action_guestNavGraph)
            }
        }
    }
}