package com.example.android_course

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.android_course.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val viewBinding by viewBinding(ActivityMainBinding::bind)


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = setupRecyclerView()



        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.viewState.collect { vs ->
                    when(vs) {
                        is MainViewModel.ViewState.Loading -> {
                            viewBinding.userRecyclerView.isVisible = false
                            viewBinding.progressBar.isVisible = true
                        }
                        is MainViewModel.ViewState.Data -> {
                            adapter.userList = vs.userList
                            adapter.notifyDataSetChanged()
                            viewBinding.userRecyclerView.isVisible = true
                            viewBinding.progressBar.isVisible = false
                        }
                    }
                }


            }
        }
    }


    private fun setupRecyclerView(): UserAdapter {
        val userRV = viewBinding.userRecyclerView
        userRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = UserAdapter()
        userRV.adapter = adapter

        adapter.notifyDataSetChanged()

        val dividerItemDecoration = DividerItemDecoration(this, RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.item_user_divider))
        userRV.addItemDecoration(dividerItemDecoration)
        return adapter
    }

}

