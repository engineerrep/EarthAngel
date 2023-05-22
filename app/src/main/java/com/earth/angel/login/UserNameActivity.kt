package com.earth.angel.login

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.earth.angel.R
import com.earth.angel.databinding.ActivityUserNameBinding
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.BaseActivity
import kotlinx.android.synthetic.main.include_top_bar.*
import org.jetbrains.anko.toast

class UserNameActivity : BaseActivity<ActivityUserNameBinding>() {

    private val maxTextNum = 15//最多显示的字数

    override fun getLayoutId(): Int =R.layout.activity_user_name

    override fun initActivity(savedInstanceState: Bundle?) {
        DataReportUtils.getInstance().report("Signup_username")

        mBinding?.run {
            showActionBar()
            tvTitleCenter.text=getString(R.string.label_Username)
            btNext.isEnabled = false
            etName?.run {
                var wordNum: CharSequence? = null
                var selectionStart: Int
                var selectionEnd: Int
                doOnTextChanged { text, _, _, _ ->
                    if(text!!.isNotEmpty()){
                        wordNum = text!!.toString().trim()

                    }
                }
                doAfterTextChanged {
                    if(it!!.isNotEmpty()){
                        maxTextNum - it!!.length
                        var str=text?.toString()?.trim()
                        tvNum?.text = str?.length.toString()
                        selectionStart = getSelectionStart()
                        selectionEnd = getSelectionEnd()
                        if (wordNum!!.length > maxTextNum) {
                            it.delete(selectionStart - 1, selectionEnd)
                            val tempSelection = selectionEnd
                            setSelection(tempSelection)
                        }

                        if(wordNum!!.length>=3){
                            tvLeast.setTextColor(ContextCompat.getColor(this@UserNameActivity, R.color.rank_un))

                        }else{
                            tvLeast.setTextColor(ContextCompat.getColor(this@UserNameActivity, R.color.red_user_name_bg))
                        }
                        btNext.isEnabled = wordNum!!.length >= 3
                        btNext.isEnabled=wordNum!!.length <16
                    }

                    }



            }
            btNext?.setOnClickListener {
                if(etName.text.toString().trim().length<=2){
                    toast(getString(R.string.text_Least))
                    return@setOnClickListener
                }
                if (DateUtils.isFastClick()){
                    val intent = Intent(this@UserNameActivity, UserHeadActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("name",etName.text.toString())
                    startActivity(intent)
                }

            }
        }
    }
}