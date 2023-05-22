package com.earth.angel.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.earth.angel.R
import kotlinx.android.synthetic.main.dialog_message.view.*
import kotlinx.android.synthetic.main.layout_pw_bottom.view.btSaveShare
import kotlinx.android.synthetic.main.layout_pw_bottom.view.pw_delete
import kotlinx.android.synthetic.main.layout_pw_bottom.view.title_tv


/**
 * 底部彈出window
 */
class DialogMessage(activity: Activity) {

    private val mActivity: Activity = activity
    private val mDecorView: ViewGroup = activity.window.decorView as ViewGroup
    private val mBgView: View = View(activity)
    private val mPopupWindow: PopupWindow = PopupWindow(mActivity)

    init {
        mBgView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mBgView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.overlay))
    }

    private lateinit var mContentView: View
    private var pwInterface: MessageInterface? = null
    private var mPwPadding: Float = 10f//Pw默认padding,，默认为10dp
    private var mTitleStr: String = "请选择" //标题字体内容，默认为"请选择"
    private var mSelectText: String? = null // 选中的字段

    fun setOnInterface(pwInterface: MessageInterface): DialogMessage {
        this.pwInterface = pwInterface
        return this
    }

    fun disMiss(): DialogMessage {
        mPopupWindow?.dismiss()
        return this
    }


    fun create(): DialogMessage {
        initView()
        mPopupWindow.contentView = mContentView
        mPopupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
        mPopupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPopupWindow.isFocusable = true//false时PopupWindow不处理返回键
        mPopupWindow.isTouchable = true//false为pw内容区域才响应点击事件
        mPopupWindow.isOutsideTouchable = false
        mPopupWindow.animationStyle = R.style.PopupWindowAnimationBottomToTop
        mPopupWindow.setOnDismissListener {
            mDecorView.removeView(mBgView)
        }
        return this
    }

    private fun initView() {
        mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_message, null)


        with(mContentView) {
            //设置圆角
            //  round_layout.delegate.cornerRadius = mCornerRadius
            // cancel_tv.delegate.cornerRadius = mCornerRadius
            //设置标题
            title_tv.text = mTitleStr
            pw_delete.setOnClickListener {
                mDecorView.removeView(mBgView)
                mPopupWindow.dismiss()

            }

            btSaveShare.setOnClickListener {
                val etNameS = etName.text.toString().trim()
                val contactDetails = etContactInfo.text.toString().trim()
                val msg = etMessage.text.toString().trim()
                if(etNameS.isNullOrEmpty()||contactDetails.isNullOrEmpty()||msg.isNullOrEmpty()){
                        return@setOnClickListener
                }
                pwInterface?.onBankString(etNameS, contactDetails, msg)
            }

        }
    }


    fun show() {
        mDecorView.addView(mBgView)
        mPopupWindow.showAtLocation(
            mBgView, Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0
        )
    }


    private fun dp2px(dpValue: Float): Int {
        val scale = mActivity.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}