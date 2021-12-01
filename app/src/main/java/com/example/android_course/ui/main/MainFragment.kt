package com.example.android_course.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.android_course.R
import com.example.android_course.databinding.FragmentMainBinding
import com.example.android_course.ui.base.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : BaseFragment(R.layout.fragment_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottom_nav = getView()?.findViewById<BottomNavigationView>(R.id.main_bottom_nav)
        val navController = (childFragmentManager.findFragmentById(R.id.mainFragmentNavigationHost) as NavHostFragment).navController
        bottom_nav?.setupWithNavController(navController)
    }
}