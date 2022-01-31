package com.example.android_course.ui.profile

import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.android_course.R
import com.example.android_course.ui.base.BaseFragment

import dagger.hilt.android.AndroidEntryPoint
import com.example.android_course.databinding.FragmentPersonBinding
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.android_course.ui.userlist.UserListViewModel
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.fragment_person) {

    private val viewBinding by viewBinding(FragmentPersonBinding::bind)

    private val viewModel: ProfileViewModel by activityViewModels()
    private val userListViewModel : UserListViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToEvents()
        subscribeToProfile()
        viewBinding.logoutButton.applyInsetter {
            type(statusBars = true) { margin() }
        }
        viewBinding.logoutButton.setOnClickListener {
            //userListViewModel.resetViewState()
            userListViewModel.isLoadingNeeded = true
            viewModel.isLoadingNeeded = true
            viewModel.logout()
        }
        if(viewModel.isLoadingNeeded) {
            Timber.d("INIT PROFILE")
            viewModel.isLoadingNeeded = false
            viewModel.loadUserProfile()
        }

    }

    private fun subscribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventsFlow().collect { event ->
                    when (event) {
                        is ProfileViewModel.Event.LogoutError -> {
                            Toast
                                .makeText(
                                    requireContext(),
                                    R.string.common_general_error_text,
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToProfile() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.viewState.collect { vs ->
                    when(vs) {
                        is ProfileViewModel.ViewState.Loading -> {
                            //viewBinding.userRecyclerView.isVisible = false
                            //viewBinding.progressBar.isVisible = true
                        }
                        is ProfileViewModel.ViewState.Data -> {
                            viewBinding.nickNameField.text = vs.userProfile.userName
                            viewBinding.firstNameField.text = vs.userProfile.firstName
                            viewBinding.secondNameField.text = vs.userProfile.lastName
                            viewBinding.emailField.text = vs.userProfile.email

                            if(vs.userProfile.picture != null) {
                                Glide.with(viewBinding.avatarImageView)
                                    .load(vs.userProfile.picture)
                                    .circleCrop()
                                    .into(viewBinding.avatarImageView) }
                            else {
                                viewBinding.avatarImageView.setImageResource(R.drawable.ic_mkn_logo_2_foreground)
                                //holder.avatarImageView.setBackgroundResource(R.drawable.ic_mkn_logo_2_background)
                            }
                            //viewBinding.userRecyclerView.isVisible = true
                            //viewBinding.progressBar.isVisible = false
                        }
                    }
                }
            }
        }
    }
}