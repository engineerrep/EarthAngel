package com.earth.angel.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.earth.angel.R

class CustomTextView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(mContext, attrs, defStyleAttr) {

    private var left_resource: Drawable? = null
    private var title: String? = null
    private var tvTitle: TextView? = null
    private var ivLeft: ImageView? = null
    private var ivMyGiftMineLine: View? = null

    private var line: Boolean? = true


    init {
        val typedArray =
            mContext.obtainStyledAttributes(attrs, R.styleable.custom_textview)
        left_resource = typedArray.getDrawable(R.styleable.custom_textview_left_resource)
        title = typedArray.getString(R.styleable.custom_textview_title_text)

        line=typedArray.getBoolean(R.styleable.custom_textview_title_text,true)
        typedArray.recycle()
        initView()
        initData()
    }


    private fun initView() {

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.layout_customview, this)
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = layoutParams
        ivLeft=findViewById(R.id.ivMyGiftMine)
        tvTitle=findViewById(R.id.tvTitle)
        ivMyGiftMineLine=findViewById(R.id.ivMyGiftMineLine)
    }

    private fun initData() {
        ivLeft?.run {
            setImageDrawable(left_resource)
        }
        tvTitle?.run {
            text=title
        }
        if(line==true){
            ivMyGiftMineLine?.visibility=View.VISIBLE
        }else{
            ivMyGiftMineLine?.visibility=View.INVISIBLE
        }
    }


}