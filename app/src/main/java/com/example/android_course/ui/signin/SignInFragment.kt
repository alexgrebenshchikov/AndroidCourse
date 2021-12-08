package com.example.android_course.ui.signin

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.databinding.FragmentSignInBinding
import com.example.android_course.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Error

@AndroidEntryPoint
class SignInFragment : BaseFragment(R.layout.fragment_sign_in) {
    private val viewBinding by viewBinding(FragmentSignInBinding::bind)
    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.backButton.setOnClickListener {
            onBackButtonPressed()
        }
        viewBinding.signInButton.setOnClickListener {
            viewModel.signIn(
                email = viewBinding.emailEditText.text?.toString() ?: "",
                password = viewBinding.passwordEditText.text?.toString() ?: ""
            )
        }
        subscribeToFormFields()
        subscribeToAuthStatus()
    }

    private fun subscribeToAuthStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInActionStateFlow().collect { vs ->
                    when (vs) {
                        is SignInViewModel.SignInActionState.Pending -> Timber.d("success")
                        is SignInViewModel.SignInActionState.ServerError -> {
                            viewBinding.underEmailTextView.text = if (vs.e.body?.email != null)
                                vs.e.body?.email!!.joinToString(separator = ", ") { e: Error ->
                                    e.message ?: ""
                                } else ""

                            viewBinding.underPasswordTextView.text = if (vs.e.body?.password != null)
                                vs.e.body?.password!!.joinToString(separator = ", ") { e: Error ->
                                    e.message ?: ""
                                } else ""

                            vs.e.body?.nonFieldErrors?.let {
                                Toast
                                    .makeText(
                                        requireContext(),
                                        it.joinToString(separator = ", ") { e: Error ->
                                            e.message ?: "" },
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                            }
                        }
                        is SignInViewModel.SignInActionState.NetworkError -> Timber.d(vs.e.toString())
                        is SignInViewModel.SignInActionState.UnknownError -> {
                            Toast
                                .makeText(
                                    requireContext(),
                                    R.string.common_general_error_text,
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                        is SignInViewModel.SignInActionState.Loading -> Timber.d("Loading")
                    }
                }
            }
        }
    }

    private fun subscribeToFormFields() {
        decideSignInButtonEnabledState(
            email = viewBinding.emailEditText.text?.toString(),
            password = viewBinding.passwordEditText.text?.toString()
        )
        viewBinding.emailEditText.doAfterTextChanged { email ->
            decideSignInButtonEnabledState(
                email = email?.toString(),
                password = viewBinding.passwordEditText.text?.toString()
            )
        }
        viewBinding.passwordEditText.doAfterTextChanged { password ->
            decideSignInButtonEnabledState(
                email = viewBinding.emailEditText.text?.toString(),
                password = password?.toString()
            )
        }
    }

    private fun decideSignInButtonEnabledState(email: String?, password: String?) {
        viewBinding.signInButton.isEnabled = !(email.isNullOrBlank() || password.isNullOrBlank())
    }

    private fun onBackButtonPressed() {
        val email = viewBinding.emailEditText.text?.toString()
        val password = viewBinding.passwordEditText.text?.toString()
        if (email.isNullOrBlank() && password.isNullOrBlank()) {
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

}
