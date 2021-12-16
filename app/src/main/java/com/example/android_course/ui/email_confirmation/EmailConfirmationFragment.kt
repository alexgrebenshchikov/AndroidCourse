package com.example.android_course.ui.email_confirmation

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
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
        Timber.d("${sharedViewModel.email} ${sharedViewModel.password}")
        viewBinding.SendCodeAgainTextView.setSendCodeAgainText {
            Timber.d("send again")
            if(sharedViewModel.sendCodeIsAllowed) {
                sendCode()
            }
        }


        if (sharedViewModel.countDownTimer == null) {
            sendCode()
        }
        subscribeToSendCodeTimer()
    }

    private fun sendCode() {
        sharedViewModel.sendCode(sharedViewModel.email)
        sharedViewModel.sendCodeIsAllowed = false
        sharedViewModel.startSendCodeTimer()
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

    private fun subscribeToSendCodeTimer() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.sendCodeTimerStateFlow().collect { vs ->
                    when(vs) {
                        0 -> {
                            viewBinding.timerTextVIew.text = getString(R.string.timer_up)
                            sharedViewModel.sendCodeIsAllowed = true
                        }
                        else -> {
                            viewBinding.timerTextVIew.text = String.format(
                                getString(R.string.send_code_again_in), vs)
                        }
                    }
                }
            }
        }
    }

    private fun TextView.setSendCodeAgainText(
        sendCodeAgainClickListener: () -> Unit
    ) {

        // Turn on ClickableSpan.
        movementMethod = LinkMovementMethod.getInstance()
        val clubRulesClickSpan =
            object : ClickableSpan() {
                override fun onClick(widget: View) = sendCodeAgainClickListener()

                @RequiresApi(Build.VERSION_CODES.M)
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = resources.getColor(R.color.purple_200, null)
                }
            }

        val spanString = SpannableString(getString(R.string.send_code_again))
        spanString.setSpan(clubRulesClickSpan, 0, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        text = spanString
    }
}