package com.earth.libbase.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

class MyConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }


}