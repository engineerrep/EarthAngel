package com.mason.libs.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.earth.angel.R


class ThemeImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var fixedColor = 0
    private var selectedColor = 0
    private val NO_COLOR = -31812
    private var typedArray: TypedArray? = getContext().obtainStyledAttributes(
        attrs,
        R.styleable.ThemeImageView
    )


    init {
        typedArray?.let {
            fixedColor = it.getColor(
                R.styleable.ThemeImageView_fixedColor,
                NO_COLOR
            )
            selectedColor = typedArray!!.getColor(
                R.styleable.ThemeImageView_selectColor,
                NO_COLOR
            )
            if ((fixedColor != NO_COLOR || selectedColor != NO_COLOR)) {
                this.setColorFilter(fixedColor, PorterDuff.Mode.SRC_IN)
            }
            it.recycle()
        }
    }

    fun setFixedColor(@ColorInt fixedColor: Int) {
        this.fixedColor = fixedColor
        if (fixedColor != NO_COLOR) {
            this.setColorFilter(fixedColor, PorterDuff.Mode.SRC_IN)
        }
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        if (pressed) {
            if (selectedColor != NO_COLOR) {
                this.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
            }
        } else if (!this.isSelected) {
            clearColorFilter()
        } else if (fixedColor != NO_COLOR) {
            this.setColorFilter(fixedColor, PorterDuff.Mode.SRC_IN)
        }
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (selected) {
            if (selectedColor != NO_COLOR) {
                this.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
            }
        } else {
            clearColorFilter()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            if (selectedColor != NO_COLOR) {
                this.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
            }
        } else {
            clearColorFilter()
        }
    }
}