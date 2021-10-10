package com.example.android_course.util

import com.example.android_course.databinding.ItemOnboardingTextBinding
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding

class OnBoardingAdapterDelegates {
}

fun onboardingTextAdapterDelegate() =
    adapterDelegateViewBinding<String, CharSequence, ItemOnboardingTextBinding>(
        viewBinding = { layoutInflater, parent ->
            ItemOnboardingTextBinding.inflate(layoutInflater, parent, false)
        },
        block = {
            bind {
                binding.textView.text = item
            }
        }
    )