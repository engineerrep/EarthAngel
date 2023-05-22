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
import com.earth.angel.dialog.dialoginterface.DialogContactsInterface
import kotlinx.android.synthetic.main.layout_pw_bottom.view.*


/**
 * 底部彈出window
 */
class DialogContacts(activity: Activity) {

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
    private val mBoolean: ArrayList<Boolean> = ArrayList()//
    private var pwInterface: DialogContactsInterface? = null
    private var mPwPadding: Float = 10f//Pw默认padding,，默认为10dp
    private var mSelectText: String? = null // 选中的字段


    fun setOnInterface(pwInterface: DialogContactsInterface): DialogContacts {
        this.pwInterface = pwInterface
        return this
    }

    fun disMiss(): DialogContacts {
        mPopupWindow?.dismiss()
        return this
    }


    fun create(): DialogContacts {
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
            pwInterface?.cancel()
        }
        return this
    }

    private fun initView() {

        mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_contacts, null)

        with(mContentView) {
            //设置圆角
            //  round_layout.delegate.cornerRadius = mCornerRadius
            // cancel_tv.delegate.cornerRadius = mCornerRadius


            btReset.setOnClickListener {
                mDecorView.removeView(mBgView)
                mPopupWindow.dismiss()
                pwInterface?.cancel()
            }
            btSaveShare.setOnClickListener {

                pwInterface?.ok()
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