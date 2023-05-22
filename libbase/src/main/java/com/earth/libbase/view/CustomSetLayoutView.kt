package com.earth.libbase.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import com.earth.libbase.R


class CustomSetLayoutView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(mContext, attrs) {
    private var tvTitle: TextView? = null
    private var tvContent: TextView? = null
    private var etContent: TextView? = null
    private var mCategoryIV: ImageView? = null

    private val arrow_resource: Drawable?
    private val title: String?
    private val needDivider: Boolean
    private val needArrow: Boolean
    private val needContent: Boolean

    private val contentHint: String?
    private var contentText: String?

    private fun initData() {
        tvTitle?.run {
            text = title;
        }
        tvContent?.run {
            if (!TextUtils.isEmpty(contentHint)) {
                this.hint = contentHint
            }
            visibility = if (needContent) View.VISIBLE else View.INVISIBLE
            if (!TextUtils.isEmpty(contentText)) {
                text = contentText
            }
        }
        arrow_resource?.run {
            mCategoryIV?.setImageDrawable(this)
        }
        needArrow?.run {
            if(this){
                mCategoryIV?.visibility=View.VISIBLE
            }else{
                mCategoryIV?.visibility=View.GONE
            }
        }
    }

    private fun initView() {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.layout_custom_setview, this)
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = layoutParams
        tvContent = findViewById(R.id.LibMineSetAddAddresstv)
        tvTitle = findViewById(R.id.LibMineSetTitle)
        mCategoryIV= findViewById(R.id.LibMineSetIV)
    }

    fun showContent(content: String?) {
        tvContent?.run {
            text = content
            visibility = View.VISIBLE
        }
    }
    fun getContent(): String =
        tvContent?.text.toString().trim()
    val text: String
        get() = tvContent!!.text.toString()

    init {
        val typedArray =
            mContext.obtainStyledAttributes(attrs, R.styleable.custom_textview)
        title = typedArray.getString(R.styleable.custom_textview_title_text)
        contentHint = typedArray.getString(R.styleable.custom_textview_hint_content_text)
        contentText = typedArray.getString(R.styleable.custom_textview_content_text)

        arrow_resource = typedArray.getDrawable(R.styleable.custom_textview_arrow_resource)
        needDivider = typedArray.getBoolean(R.styleable.custom_textview_need_divider, true)
        needArrow =
            typedArray.getBoolean(R.styleable.custom_textview_needArrow, true)

        needContent =
            typedArray.getBoolean(R.styleable.custom_textview_needContent, true)

        typedArray.recycle()
        initView()
        initData()
    }

  /*  // SET 方法  双向数据绑定的方法
    companion object {
        @BindingAdapter("y_content")
        @JvmStatic
        fun getStr(ceTv: CustomTextView?, content: String?) {
            if (ceTv != null) {
                if (content!!.isNotEmpty()) {
                    ceTv.tvContent?.text = content
                }
            }
        }
    }*/
}