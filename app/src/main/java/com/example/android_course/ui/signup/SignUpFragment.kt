package com.example.android_course.ui.signup

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.android_course.R
import com.example.android_course.ui.base.BaseFragment
import com.example.android_course.databinding.FragmentSignUpBinding


class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {
    private val viewBinding by viewBinding(FragmentSignUpBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.confirmButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_signUpFragment_to_emailConfirmationFragment)
        }

    }

}