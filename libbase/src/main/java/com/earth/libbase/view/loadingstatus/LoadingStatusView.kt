package com.earth.libbase.view.loadingstatus

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.earth.libbase.R
import com.earth.libbase.base.drawableValue
import com.earth.libbase.base.stringValue


class LoadingStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LoadingDrawableTextView(context, attrs, defStyleAttr) {

    var errorCustomStr = ""
    var errorReload: ErrorReload? = null

    init {
        injectRequestStatus(LoadingStatusCode.Loading)
    }

    fun injectRequestStatus(status: LoadingStatusCode) {
        val statusDrawable: Drawable?

        when (status) {
            LoadingStatusCode.Loading -> {
                statusDrawable = ContextCompat.getDrawable(context, R.mipmap.loading)
                text = context.stringValue(R.string.loading)
                setTextColor(ContextCompat.getColor(context, R.color.text_Unblack))

            }

            LoadingStatusCode.Empty -> {
                statusDrawable = context.drawableValue( R.mipmap.base_empty_pocket)
                text = context.stringValue(R.string.empty_data)
                setTextColor(ContextCompat.getColor(context, R.color.BaseThemColor))
            }

            LoadingStatusCode.Error -> {
                statusDrawable = context.drawableValue( R.mipmap.base_empty_pocket)
                text = context.stringValue(R.string.reload_data)
                setTextColor(ContextCompat.getColor(context, R.color.BaseThemColor))

            }
            LoadingStatusCode.ErrorCustom -> {
                statusDrawable = context.drawableValue( R.mipmap.base_empty_pocket)
                text = errorCustomStr
                setTextColor(ContextCompat.getColor(context, R.color.BaseThemColor))

            }

            LoadingStatusCode.Succeed -> {
                isVisible = false
                return
            }
        }

        isVisible = true
        statusDrawable?.setBounds(
            0,
            0,
            statusDrawable.minimumWidth,
            statusDrawable.minimumHeight
        )
        compoundDrawablePadding = 12
        setCompoundDrawables(null, statusDrawable, null, null)

        setOnClickListener {
            if (status == LoadingStatusCode.Error) {
                errorReload?.reload()
            }
        }
    }
}

enum class LoadingStatusCode { Empty, Error, Loading, Succeed, ErrorCustom }