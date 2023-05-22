package com.earth.libbase.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText


class ClearEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) ,TextWatcher, View.OnFocusChangeListener {
    private var hasFocus = false
    private var mClearDrawable: Drawable?=null
    private var mTextWatcher: TextWatcher? = null
    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    init {
        hasFocus=hasFocus()
        mClearDrawable= compoundDrawables[2]
        if(mClearDrawable==null){
            mClearDrawable= compoundDrawables[2]

        }
        if(mClearDrawable!=null){
            mClearDrawable?.setBounds(0,0,28,mClearDrawable!!.intrinsicHeight)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action==MotionEvent.ACTION_UP&&mClearDrawable!=null){
            var left=width-totalPaddingRight-compoundPaddingRight
            var touchable=event.getX()>left && event.getX()< width
            if(touchable){
                this.setText("")
            }

        }

        return super.onTouchEvent(event)

    }

    /**
     * 当ClearEditText焦点发生变化的时候：
     * 有焦点并且输入的文本内容不为空时则显示清除按钮
     */
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        this.hasFocus = hasFocus
        setClearIconVisible(hasFocus && !TextUtils.isEmpty(text))
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener?.onFocusChange(v, hasFocus)
        }
    }

    /**
     * 当输入框里面内容发生变化的时候：
     * 有焦点并且输入的文本内容不为空时则显示清除按钮
     */
    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        setClearIconVisible(hasFocus && s.length > 0)
        if (mTextWatcher != null) {
            mTextWatcher?.onTextChanged(s, start, count, after)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (mTextWatcher != null) {
            mTextWatcher?.beforeTextChanged(s, start, count, after)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (mTextWatcher != null) {
            mTextWatcher?.afterTextChanged(s)
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected fun setClearIconVisible(visible: Boolean) {
        if (mClearDrawable == null) return
        val right = if (visible) mClearDrawable else null
        setCompoundDrawables(
            compoundDrawables[0],
            compoundDrawables[1], right, compoundDrawables[3]
        )
    }

    /**
     * 1、是自己本身的话则调用父类的实现，
     * 2、是外部设置的就自己处理回调回去
     */
    override fun setOnFocusChangeListener(l: OnFocusChangeListener) {
        if (l is ClearEditText) {
            super.setOnFocusChangeListener(l)
        } else {
            mOnFocusChangeListener = l
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher) {
        if (watcher is ClearEditText) {
            super.addTextChangedListener(watcher)
        } else {
            mTextWatcher = watcher
        }
    }
}