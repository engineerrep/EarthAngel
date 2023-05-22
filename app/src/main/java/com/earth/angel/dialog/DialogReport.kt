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
import com.earth.libbase.entity.MemberEntity
import kotlinx.android.synthetic.main.dialog_report.view.*


/**
 * 底部彈出window
 */
class DialogReport(activity: Activity) {

    private val mActivity: Activity = activity
    private val mDecorView: ViewGroup = activity.window.decorView as ViewGroup
    private val mBgView: View = View(activity)
    private val mPopupWindow: PopupWindow = PopupWindow(mActivity)
    var listMenbertList: ArrayList<MemberEntity> = ArrayList()

    init {
        mBgView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mBgView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.overlay))
    }

    private lateinit var mContentView: View
    private var pwInterface: ReportInterface? = null
    private var mPwPadding: Float = 10f//Pw默认padding,，默认为10dp
    private var mSelectText: String? = null // 选中的字段

    fun setOnInterface(pwInterface: ReportInterface): DialogReport {
        this.pwInterface = pwInterface
        return this
    }

    fun disMiss(): DialogReport {
        mPopupWindow?.dismiss()
        return this
    }

    fun create(): DialogReport {
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

        mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_report, null)

        with(mContentView) {
            //设置圆角
            //  round_layout.delegate.cornerRadius = mCornerRadius
            // cancel_tv.delegate.cornerRadius = mCornerRadius
            //设置标题
            tvCancel.setOnClickListener {
                mDecorView.removeView(mBgView)
                mPopupWindow.dismiss()

            }
            tvItems.setOnClickListener {
                pwInterface?.onBankString(tvItems.text.toString())
            }
            tvFraud.setOnClickListener {
                pwInterface?.onBankString(tvFraud.text.toString())
            }
            tvSpam.setOnClickListener {
                pwInterface?.onBankString(tvSpam.text.toString())
            }
            tvInappropriate.setOnClickListener {
                pwInterface?.onBankString(tvInappropriate.text.toString())
            }
            tvOther.setOnClickListener {
                pwInterface?.onBankString(tvOther.text.toString())
            }



        }
    }


    fun show() {
        mDecorView.addView(mBgView)
        mPopupWindow.showAtLocation(
            mBgView, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0
        )
    }


    private fun dp2px(dpValue: Float): Int {
        val scale = mActivity.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}