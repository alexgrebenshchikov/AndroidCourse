package com.example.android_course.ui.userlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.util.UserAdapter
import com.example.android_course.databinding.FragmentUserListBinding
import com.example.android_course.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserListFragment : BaseFragment(R.layout.fragment_user_list) {
    private val viewModel: UserListViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentUserListBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = setupRecyclerView()



        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.viewState.collect { vs ->
                    when(vs) {
                        is UserListViewModel.ViewState.Loading -> {
                            viewBinding.userRecyclerView.isVisible = false
                            viewBinding.progressBar.isVisible = true
                        }
                        is UserListViewModel.ViewState.Data -> {
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
        userRV.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = UserAdapter()
        userRV.adapter = adapter

        adapter.notifyDataSetChanged()

        val dividerItemDecoration = DividerItemDecoration(this.requireContext(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.item_user_divider))
        userRV.addItemDecoration(dividerItemDecoration)
        return adapter
    }

}