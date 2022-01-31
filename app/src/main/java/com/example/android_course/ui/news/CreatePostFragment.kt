package com.example.android_course.ui.news

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.BuildConfig
import com.example.android_course.R
import com.example.android_course.databinding.FragmentCreatePostBinding
import com.example.android_course.ui.MainActivity
import com.example.android_course.ui.base.BaseFragment
import com.google.common.base.Joiner.on
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.vansuita.pickimage.listeners.IPickCancel

import com.vansuita.pickimage.bean.PickResult

import com.vansuita.pickimage.listeners.IPickResult
import org.koin.androidx.viewmodel.ext.android.viewModel

import timber.log.Timber


@AndroidEntryPoint
class CreatePostFragment : BaseFragment(R.layout.fragment_create_post){
    private val viewBinding by viewBinding(FragmentCreatePostBinding::bind)
    private val viewModel : CreatePostViewModel by viewModels()
    private val uploadImageViewModel : UploadImageViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.progressBar.isVisible = false
        subscribeToFormFields()
        subscribeToCreatePostStatus()
        subscribeToUploadImageStatus()
        viewBinding.backButton.setOnClickListener {
            onBackButtonPressed()
        }
        viewBinding.postButton.setOnClickListener {
            viewModel.createPost(
                title = viewBinding.postTitleEditText.text?.toString() ?: "",
                text = viewBinding.postTextEditText.text?.toString() ?: "",
                image = uploadImageViewModel.imageLink,
                link = viewBinding.postLinkEditText.text?.toString() ?: ""
            )
        }
        viewBinding.loadImageButton.setOnClickListener {
            PickImageDialog.build(PickSetup())
                .setOnPickResult {
                    if(it.error == null)  {
                        viewBinding.postImageView.setImageURI(it.uri)
                        Timber.d(it.uri.path)
                        uploadImageViewModel.uploadImage(it.uri)
                    } else {
                        Timber.d("failed to load photo")
                    }
                }
                .setOnPickCancel {
                    //TODO: do what you have to if user clicked cancel
                }.show(fragmentManager)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackButtonPressed()
                }
            }
        )
    }


    private fun onBackButtonPressed() {
        val title = viewBinding.postTitleEditText.text?.toString()
        val text = viewBinding.postTextEditText.text?.toString()
        val link = viewBinding.postLinkEditText.text?.toString()
        if (title.isNullOrBlank()
            && text.isNullOrBlank()
            && !uploadImageViewModel.imageIsLoaded
            && link.isNullOrBlank()
        ) {
            findNavController().popBackStack()
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.sign_in_back_alert_dialog_text)
            .setNegativeButton(R.string.sign_in_back_alert_dialog_cancel_button_text) { dialog, _ ->
                dialog?.dismiss()
            }
            .setPositiveButton(R.string.sign_in_back_alert_dialog_ok_button_text) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun subscribeToFormFields() {
        decideSignUpButtonEnabledState(
            title = viewBinding.postTitleEditText.text?.toString(),
            text = viewBinding.postTextEditText.text?.toString(),
            link = viewBinding.postLinkEditText.text?.toString()
        )
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                decideSignUpButtonEnabledState(
                    title = viewBinding.postTitleEditText.text?.toString(),
                    text = viewBinding.postTextEditText.text?.toString(),
                    link = viewBinding.postLinkEditText.text?.toString()
                )
            }

        }
        viewBinding.postTitleEditText.addTextChangedListener(watcher)
        viewBinding.postTextEditText.addTextChangedListener(watcher)
        viewBinding.postLinkEditText.addTextChangedListener(watcher)

    }

    private fun decideSignUpButtonEnabledState(
        title: String?,
        text: String?,
        link: String?,

    ) {
        viewBinding.postButton.isEnabled = !title.isNullOrBlank()
                && !text.isNullOrBlank()
                && uploadImageViewModel.imageIsLoaded
                && !link.isNullOrBlank()

    }

    private fun subscribeToCreatePostStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { vs ->
                    when(vs) {
                        is CreatePostViewModel.ViewState.Loading -> {
                            viewBinding.progressBar.isVisible = true
                        }
                        is CreatePostViewModel.ViewState.Success -> {
                            findNavController().popBackStack()
                        }
                        is CreatePostViewModel.ViewState.Error -> {
                            viewBinding.progressBar.isVisible = false
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

    private fun subscribeToUploadImageStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                uploadImageViewModel.viewState.collect { vs ->
                    when(vs) {
                        is UploadImageViewModel.ViewState.Loading -> {
                            viewBinding.progressBar.isVisible = true
                            Timber.d("image loading")
                        }
                        is UploadImageViewModel.ViewState.Success -> {
                            viewBinding.progressBar.isVisible = false
                            decideSignUpButtonEnabledState(
                                title = viewBinding.postTitleEditText.text?.toString(),
                                text = viewBinding.postTextEditText.text?.toString(),
                                link = viewBinding.postLinkEditText.text?.toString()
                            )
                            Timber.d("image success")
                        }
                        is UploadImageViewModel.ViewState.Error -> {
                            Timber.d("image error")
                            viewBinding.progressBar.isVisible = false
                            Toast
                                .makeText(
                                    requireContext(),
                                    vs.msg,
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                    }
                }
            }
        }
    }


}