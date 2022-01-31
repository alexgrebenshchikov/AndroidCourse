package com.example.android_course.ui.userlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.util.UserAdapter
import com.example.android_course.databinding.FragmentUserListBinding
import com.example.android_course.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class UserListFragment : BaseFragment(R.layout.fragment_user_list) {
    private val viewModel: UserListViewModel by activityViewModels()
    private val viewBinding by viewBinding(FragmentUserListBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = setupRecyclerView()


        viewBinding.progressBar.isVisible = false
        if(!viewModel.isAuthorizationFailed)
            viewBinding.authorizationFailedLabel.isVisible = false

        subscribeToViewState(adapter)
        subscribeToSignInState()

        if(viewModel.isLoadingNeeded) {
            Timber.d("INIT USER LIST")
            viewModel.isLoadingNeeded = false
            viewModel.loadUsers()
        }

    }


    private fun subscribeToViewState(adapter : UserAdapter) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.viewState.collect { vs ->
                    when (vs) {
                        is UserListViewModel.ViewState.Pending -> {

                        }
                        is UserListViewModel.ViewState.Loading -> {
                            Timber.d("loading")
                            viewBinding.userRecyclerView.isVisible = false
                            viewBinding.progressBar.isVisible = true
                        }
                        is UserListViewModel.ViewState.Data -> {
                            adapter.userList = vs.userList
                            adapter.notifyDataSetChanged()
                            viewBinding.userRecyclerView.isVisible = true
                            viewBinding.progressBar.isVisible = false
                        }
                        is UserListViewModel.ViewState.ServerError -> {
                            Timber.d("server er")
                            if (viewModel.tryAgain) {
                                viewModel.tryAgain = false
                                viewModel.loadUsers()
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToSignInState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetSignInState()
                viewModel.signInState.collect { vs ->
                    when(vs) {
                        is UserListViewModel.ViewState.ServerError -> {
                            Timber.d("server sign in")
                            viewBinding.progressBar.isVisible = false
                            AlertDialog.Builder(requireContext())
                                .setTitle(getString(R.string.Authorization_error))
                                .setNegativeButton(R.string.sign_in_back_alert_dialog_cancel_button_text) { dialog, _ ->
                                    viewBinding.authorizationFailedLabel.isVisible = true
                                    viewModel.isAuthorizationFailed = true
                                    dialog?.dismiss()
                                }
                                .setPositiveButton(R.string.sign_in_back_alert_dialog_ok_button_text) { dialog, _ ->
                                    viewModel.isLoadingNeeded = true
                                    viewModel.logout()
                                }
                                .show()
                        }
                        else -> {}
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