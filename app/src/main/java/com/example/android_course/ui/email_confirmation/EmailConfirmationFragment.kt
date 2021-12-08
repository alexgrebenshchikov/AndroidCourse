package com.example.android_course.ui.email_confirmation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.databinding.FragmentEmailConfirmationBinding
import com.example.android_course.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class EmailConfirmationFragment : BaseFragment(R.layout.fragment_email_confirmation) {
    private val viewBinding by viewBinding(FragmentEmailConfirmationBinding::bind)

    private val sharedViewModel: SignUpEmailConfirmationViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        viewBinding.sendCodeButton.setOnClickListener {
            sharedViewModel.verifyRegistrationCode(sharedViewModel.code, "", "")
        }
        subscribeToVerifyRegistrationCodeStatus()
        viewBinding.sendCodeButton.isEnabled = false
        viewBinding.codeEditText.onVerificationCodeFilledChangeListener = { isFilled ->

            viewBinding.sendCodeButton.isEnabled = isFilled
        }
        viewBinding.codeEditText.onVerificationCodeFilledListener = { code ->
            Timber.d(code)
            sharedViewModel.code = code
        }
        sharedViewModel.sendCode(sharedViewModel.email)
        Timber.d("${sharedViewModel.email} ${sharedViewModel.password}")
    }

    private fun subscribeToVerifyRegistrationCodeStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.verifyRegistrationCodeActionStateFlow().collect { vs ->
                    when (vs) {
                        is SignUpEmailConfirmationViewModel.VerifyRegistrationCodeActionState.Success -> {
                            Timber.d("success")
                            sharedViewModel.resetVerifyRegistrationCodeActionStateFlow()
                            sharedViewModel.signUp(
                                sharedViewModel.firstname,
                                sharedViewModel.lastname,
                                sharedViewModel.nickname,
                                sharedViewModel.email,
                                sharedViewModel.password,
                                sharedViewModel.code
                            )
                        }
                        is SignUpEmailConfirmationViewModel.VerifyRegistrationCodeActionState.ServerError -> {
                            Toast
                                .makeText(
                                    requireContext(),
                                    vs.e.body?.nonFieldErrors?.first()?.message,
                                    Toast.LENGTH_LONG
                                )
                                .show()
                            sharedViewModel.resetSendVerificationCodeStateFlow()
                        }
                        is SignUpEmailConfirmationViewModel.VerifyRegistrationCodeActionState.NetworkError -> Timber.d(
                            vs.e.toString()
                        )
                        is SignUpEmailConfirmationViewModel.VerifyRegistrationCodeActionState.UnknownError -> {
                            Toast
                                .makeText(
                                    requireContext(),
                                    R.string.common_general_error_text,
                                    Toast.LENGTH_LONG
                                )
                                .show()
                            sharedViewModel.resetSendVerificationCodeStateFlow()
                        }
                        is SignUpEmailConfirmationViewModel.VerifyRegistrationCodeActionState.Loading -> Timber.d(
                            "Loading"
                        )
                        is SignUpEmailConfirmationViewModel.VerifyRegistrationCodeActionState.Pending -> {
                        }
                    }
                }
            }
        }
    }
}