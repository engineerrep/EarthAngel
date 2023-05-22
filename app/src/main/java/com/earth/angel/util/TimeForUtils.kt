package com.earth.angel.util

import android.R
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

object TimeForUtils {
    fun getTime(long: Long): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sd = sdf.format(Date(java.lang.String.valueOf(long).toLong())) // 时间戳转换成时间
        return sd
    }
    fun getGiftTime(long: Long): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val sd = sdf.format(Date(java.lang.String.valueOf(long).toLong())) // 时间戳转换成时间
        return sd
    }
    fun getGiftDtailsTime(long: Long): String{
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        val sd = sdf.format(Date(java.lang.String.valueOf(long).toLong())) // 时间戳转换成时间
        return sd
    }
    fun isNumericZidai(str: String): Boolean {
        for (i in str.indices) {
            println(str[i])
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    fun setSpan(
         context: Context,
        tv: TextView,
        @StringRes strId: Int,
        underLine: Boolean,
        @ColorRes color: Int,
        start: Int,
        end: Int,
        textSize: Int,
        listener: View.OnClickListener?
    ) {
        tv.highlightColor = ContextCompat.getColor(context, R.color.transparent)
        val str = context.getString(strId)
        val info = SpannableString(str)
        val startNum = if (start <= -1) 0 else start
        val endNum = if (end <= -1) str.length else end
        if (textSize > 0) {
            info.setSpan(
                AbsoluteSizeSpan(sp2px(context, textSize.toFloat())), startNum,
                endNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        info.setSpan(
            object : ClickableSpan() {
                /**
                 * 重写父类点击事件
                 */
                override fun onClick(widget: View) {
                    listener?.onClick(widget)
                }
                /**
                 * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
                 */
                override fun updateDrawState(ds: TextPaint) {
                    var colorRes = color
                    if (color == -1) colorRes = Color.BLUE
                    ds.color = ContextCompat.getColor(context, colorRes)
                    ds.isUnderlineText = underLine
                }
            }, startNum,
            endNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv.text = info
        tv.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
    fun setText(
        context: Context,
        content: String,
        tv: TextView,
        startIndex1: Int,
        endIndex1: Int,
        listener1: View.OnClickListener?
    ) {
        val tSpannableStringBuilder = SpannableStringBuilder()
        tSpannableStringBuilder.append(content)

        //点击1
        tSpannableStringBuilder.setSpan(
                AbsoluteSizeSpan(sp2px(context, 15.toFloat())), startIndex1,
            endIndex1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )


        tSpannableStringBuilder.setSpan(
            object : ClickableSpan() {
                /**
                 * 重写父类点击事件
                 */
                override fun onClick(widget: View) {
                    listener1?.onClick(widget)
                }
                /**
                 * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
                 */
                override fun updateDrawState(ds: TextPaint) {
                    ds.typeface= Typeface.DEFAULT_BOLD
                    ds.color = Color.parseColor("#FF83D69E")
                    ds.isUnderlineText = true
                }
            }, startIndex1,
            endIndex1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )






        //配置给TextView
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.text = tSpannableStringBuilder
    }
}