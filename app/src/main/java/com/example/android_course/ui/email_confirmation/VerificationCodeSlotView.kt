package com.example.android_course.ui.email_confirmation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.android_course.databinding.VerificationCodeSlotViewBinding


class VerificationCodeSlotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

   val viewBinding =
        VerificationCodeSlotViewBinding.inflate(LayoutInflater.from(context), this)
}