package com.earth.libbase.view.loadingstatus

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

open class LoadingDrawableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        LoadingDrawableHelper.preDraw(this, canvas)
        super.onDraw(canvas)
    }
}