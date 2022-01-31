package com.example.android_course.ui.signup

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.ui.base.BaseFragment
import com.example.android_course.databinding.FragmentSignUpBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import android.text.Spanned

import android.text.SpannableString

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.android_course.data.network.response.error.MyError
import com.example.android_course.ui.email_confirmation.SignUpEmailConfirmationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Error


@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {
    private val viewBinding by viewBinding(FragmentSignUpBinding::bind)

    private val sharedViewModel: SignUpEmailConfirmationViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.signUpButton.setOnClickListener {
            sharedViewModel.email = viewBinding.emailEditText.text?.toString() ?: ""
            sharedViewModel.password = viewBinding.passwordEditText.text?.toString() ?: ""
            sharedViewModel.firstname = viewBinding.firstnameEditText.text?.toString() ?: ""
            sharedViewModel.lastname = viewBinding.lastnameEditText.text?.toString() ?: ""
            sharedViewModel.nickname = viewBinding.nicknameEditText.text?.toString() ?: ""
            sharedViewModel.signUp(
                firstname = viewBinding.firstnameEditText.text?.toString() ?: "",
                lastname = viewBinding.lastnameEditText.text?.toString() ?: "",
                userName = viewBinding.nicknameEditText.text?.toString() ?: "",
                email = viewBinding.emailEditText.text?.toString() ?: "",
                password = viewBinding.passwordEditText.text?.toString() ?: "",
                verificationCode = ""
            )


            //findNavController().navigate(R.id.action_signUpFragment_to_emailConfirmationFragment)
        }
        viewBinding.termsAndConditionsCheckBox.setClubRulesText {
            Timber.d("checkbox")
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://policies.google.com/terms")
                )
            )
        }
        viewBinding.backButton.setOnClickListener { onBackButtonPressed() }

        viewBinding.progressBar.isVisible = false
        subscribeToFormFields()

        subscribeToSignUpStatus()
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

        //subscribeToEvents()
    }

    private fun onBackButtonPressed() {
        val firstname = viewBinding.firstnameEditText.text?.toString()
        val lastname = viewBinding.lastnameEditText.text?.toString()
        val nickname = viewBinding.nicknameEditText.text?.toString()
        val email = viewBinding.emailEditText.text?.toString()
        val password = viewBinding.passwordEditText.text?.toString()
        if (firstname.isNullOrBlank()
            && lastname.isNullOrBlank()
            && nickname.isNullOrBlank()
            && email.isNullOrBlank()
            && password.isNullOrBlank()
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


    private fun subscribeToSignUpStatus() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.signUpActionStateFlow().collect { vs ->
                    when (vs) {
                        is SignUpEmailConfirmationViewModel.SignUpActionState.ServerError -> {
                            viewBinding.progressBar.isVisible = false
                            viewBinding.underEmailTextView.text = if (vs.e.body?.email != null)
                                vs.e.body?.email!!.joinToString(separator = ", ") { e: MyError ->
                                    e.message ?: ""
                                } else ""

                            viewBinding.underNickNameTextView.text =
                                if (vs.e.body?.userName != null)
                                    vs.e.body?.userName!!.joinToString(separator = ", ") { e: MyError ->
                                        e.message ?: ""
                                    } else ""

                        }

                        is SignUpEmailConfirmationViewModel.SignUpActionState.NetworkError -> {
                            viewBinding.progressBar.isVisible = false
                        }
                        is SignUpEmailConfirmationViewModel.SignUpActionState.UnknownError -> {
                            viewBinding.progressBar.isVisible = false
                            Toast
                                .makeText(
                                    requireContext(),
                                    R.string.common_general_error_text,
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                        is SignUpEmailConfirmationViewModel.SignUpActionState.Loading -> {
                            viewBinding.progressBar.isVisible = true
                        }
                        is SignUpEmailConfirmationViewModel.SignUpActionState.Success -> {
                            sharedViewModel.resetSignUpActionStateFlow()
                            sharedViewModel.countDownTimer?.cancel()
                            sharedViewModel.countDownTimer = null
                            sharedViewModel.sendCodeIsAllowed = true
                            try {
                                val bundle = bundleOf(
                                    "email" to sharedViewModel.email,
                                    "password" to sharedViewModel.password
                                )
                                findNavController().navigate(
                                    R.id.action_signUpFragment_to_signInFragment,
                                    bundle
                                )
                            } catch (_: Throwable) {
                            }
                        }
                        is SignUpEmailConfirmationViewModel.SignUpActionState.Pending -> {
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToFormFields() {
        decideSignUpButtonEnabledState(
            firstname = viewBinding.firstnameEditText.text?.toString(),
            lastname = viewBinding.lastnameEditText.text?.toString(),
            nickname = viewBinding.nicknameEditText.text?.toString(),
            email = viewBinding.emailEditText.text?.toString(),
            password = viewBinding.passwordEditText.text?.toString(),
            termsIsChecked = viewBinding.termsAndConditionsCheckBox.isChecked
        )
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                decideSignUpButtonEnabledState(
                    firstname = viewBinding.firstnameEditText.text?.toString(),
                    lastname = viewBinding.lastnameEditText.text?.toString(),
                    nickname = viewBinding.nicknameEditText.text?.toString(),
                    email = viewBinding.emailEditText.text?.toString(),
                    password = viewBinding.passwordEditText.text?.toString(),
                    termsIsChecked = viewBinding.termsAndConditionsCheckBox.isChecked
                )
            }

        }
        viewBinding.firstnameEditText.addTextChangedListener(watcher)
        viewBinding.lastnameEditText.addTextChangedListener(watcher)
        viewBinding.nicknameEditText.addTextChangedListener(watcher)
        viewBinding.emailEditText.addTextChangedListener(watcher)
        viewBinding.passwordEditText.addTextChangedListener(watcher)
        viewBinding.termsAndConditionsCheckBox.setOnClickListener {
            decideSignUpButtonEnabledState(
                firstname = viewBinding.firstnameEditText.text?.toString(),
                lastname = viewBinding.lastnameEditText.text?.toString(),
                nickname = viewBinding.nicknameEditText.text?.toString(),
                email = viewBinding.emailEditText.text?.toString(),
                password = viewBinding.passwordEditText.text?.toString(),
                termsIsChecked = viewBinding.termsAndConditionsCheckBox.isChecked
            )
        }
    }

    private fun decideSignUpButtonEnabledState(
        firstname: String?,
        lastname: String?,
        nickname: String?,
        email: String?,
        password: String?,
        termsIsChecked: Boolean
    ) {
        viewBinding.signUpButton.isEnabled = !firstname.isNullOrBlank()
                && !lastname.isNullOrBlank()
                && !nickname.isNullOrBlank()
                && !email.isNullOrBlank()
                && !password.isNullOrBlank()
                && termsIsChecked
    }

    private fun CheckBox.setClubRulesText(
        clubRulesClickListener: () -> Unit
    ) {

        // Turn on ClickableSpan.
        movementMethod = LinkMovementMethod.getInstance()
        val clubRulesClickSpan =
            object : ClickableSpan() {
                override fun onClick(widget: View) = clubRulesClickListener()

                @RequiresApi(Build.VERSION_CODES.M)
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = resources.getColor(R.color.purple_200, null)
                }
            }

        val spanString = SpannableString(getString(R.string.sign_up_terms_and_conditions_template))
        spanString.setSpan(clubRulesClickSpan, 10, 33, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        text = spanString
    }

}