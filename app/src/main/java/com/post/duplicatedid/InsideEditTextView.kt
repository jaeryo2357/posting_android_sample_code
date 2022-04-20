package com.post.duplicatedid

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.post.duplicatedid.databinding.ViewInsideEditextBinding

class InsideEditTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(context, attributeSet, defStyleRes) {
    private val binding = ViewInsideEditextBinding.inflate(
        LayoutInflater.from(context), this, true
    )
}