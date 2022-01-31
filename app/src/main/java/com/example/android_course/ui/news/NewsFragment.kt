package com.example.android_course.ui.news

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.databinding.FragmentNewsBinding
import com.example.android_course.ui.base.BaseFragment
import com.example.android_course.util.UserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NewsFragment : BaseFragment(R.layout.fragment_news) {

    private val viewBinding by viewBinding(FragmentNewsBinding::bind)
    private val viewModel: NewsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.loadPosts()
        val adapter = setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                if(loadStates.refresh !is LoadState.Loading) {
                    viewBinding.swipeContainer.isRefreshing = false
                }
                if(loadStates.refresh is LoadState.Error)
                    adapter.refresh()
            }
        }

        viewBinding.swipeContainer.setOnRefreshListener {
            Timber.d("refresh")
            adapter.refresh()
        }


        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.postsFlow.collectLatest { pagingData ->
                viewBinding.progressBar.isVisible = false
                adapter.submitData(pagingData)
            }
        }

        viewBinding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_newsFragment_to_createPostFragment)
        }

    }

    private fun setupRecyclerView(): PostAdapter {
        val postRV = viewBinding.postRecyclerView
        postRV.layoutManager = LinearLayoutManager(this.requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = PostAdapter()
        postRV.adapter = adapter

        adapter.notifyDataSetChanged()

        val dividerItemDecoration = DividerItemDecoration(this.requireContext(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.item_user_divider))
        postRV.addItemDecoration(dividerItemDecoration)
        return adapter
    }
}