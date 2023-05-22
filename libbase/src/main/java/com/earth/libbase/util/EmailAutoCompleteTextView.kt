package com.earth.libbase.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.earth.libbase.R


class EmailAutoCompleteTextView : androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    private var emailSuffix = arrayOf(
        "@gmail.com", "@yahoo.com",
        "@icloud.com","@hotmail.com","@outlook.com"
    )

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(context)
    }


    fun setCanAutoComplete(canAutoComplete: Boolean) {
        if (!canAutoComplete) {
            this.threshold = 1000
        } else {
            this.threshold = 1
        }
    }

    private fun init(context: Context) {
        setAdapter(
            EmailAutoCompleteAdapter(
                context,
                R.layout.email_auto_complete_item,
                emailSuffix
            )
        )
        //start autocomplete after input 1 char
        this.threshold = 1
        this.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val text = this@EmailAutoCompleteTextView.text.toString()
                //restart autocomplete when retrieve the focus
                if ("" != text) performFiltering(text, 0)
            }
        }
    }

    override fun replaceText(text: CharSequence) {
        Log.i(TAG, text.toString())
        var t = this.text.toString()
        val index = t.indexOf('@')
        if (index != -1) t = t.substring(0, index)
        super.replaceText(t + text)
    }

    override fun performFiltering(text: CharSequence, keyCode: Int) {
        Log.i(TAG, "$text   $keyCode")
        val t = text.toString()
        val index = t.indexOf('@')
        if (index == -1) {
            dismissDropDown()
        } else {
            super.performFiltering(t.substring(index), keyCode)
        }
    }

    private inner class EmailAutoCompleteAdapter(
        context: Context?,
        textViewResourceId: Int,
        email_s: Array<String>?
    ) : ArrayAdapter<String?>(
        context!!, textViewResourceId, email_s!!
    ) {
        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            Log.i(TAG, "in GetView")
            var v = convertView
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.email_auto_complete_item, null)
            }
            val tv = v!!.findViewById<TextView>(R.id.tv)
            if (position == 0) {
                tv.setBackgroundResource(R.color.color_email_auto_complete_first)
            } else {
                tv.setBackgroundResource(R.color.color_email_auto_complete_other)
            }
            var t = this@EmailAutoCompleteTextView.text.toString()
            val index = t.indexOf('@')
            if (index != -1) {
                t = t.substring(0, index)
            }
            tv.text = t + getItem(position)
            Log.i(TAG, tv.text.toString())
            return v
        }
    }

    companion object {
        private const val TAG = "EmailAutoCompleteText"
    }
}