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
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.dialog.adapter.DialogUserdapter
import com.earth.angel.util.ScreenUtil
import com.earth.libbase.entity.MemberEntity
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.dialog_member.view.*
import kotlinx.android.synthetic.main.layout_pw_bottom.view.pw_delete


/**
 * 底部彈出window
 */
class DialogMember(activity: Activity) {

    private val mActivity: Activity = activity
    private val mDecorView: ViewGroup = activity.window.decorView as ViewGroup
    private val mBgView: View = View(activity)
    private var mAdapter: DialogUserdapter?=null
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
    private var pwInterface: MemberInterface? = null
    private var mPwPadding: Float = 10f//Pw默认padding,，默认为10dp
    private var mSelectText: String? = null // 选中的字段

    fun setOnInterface(pwInterface: MemberInterface): DialogMember {
        this.pwInterface = pwInterface
        return this
    }

    fun disMiss(): DialogMember {
        mPopupWindow?.dismiss()
        return this
    }
    //设置列表数据源
    fun addtList(list: List<MemberEntity>): DialogMember {
        listMenbertList.addAll(list)
        mAdapter?.notifyDataSetChanged()
        mContentView?.recyclerViewMember?.let { it1 ->
            it1.finishRefresh(true)
            it1.finishLoadMore(true)
        }
        return this
    }
    fun clear(): DialogMember {
        listMenbertList.clear()
        mAdapter?.notifyDataSetChanged()
        return this
    }
    fun anoteList(position: Int,isConcern: Boolean): DialogMember {
        listMenbertList[position].isConcern=isConcern
        mAdapter?.notifyDataSetChanged()
        return this
    }
    //geng
    fun notifilist(position: Int): DialogMember {
        listMenbertList?.let {
            listMenbertList[position].isConcern=true
            mAdapter?.notifyDataSetChanged()
        }
        return this
    }
    //geng
    fun update(): DialogMember {
            mAdapter?.notifyDataSetChanged()

        return this
    }
    fun create(): DialogMember {
        initView()
        mPopupWindow.contentView = mContentView
        mPopupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
        mPopupWindow.height = 2*ScreenUtil.getHeight()/3
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

        mContentView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_member, null)

        with(mContentView) {
            //设置圆角
            //  round_layout.delegate.cornerRadius = mCornerRadius
            // cancel_tv.delegate.cornerRadius = mCornerRadius
            //设置标题
            pw_delete.setOnClickListener {
                mDecorView.removeView(mBgView)
                mPopupWindow.dismiss()

            }
            recyclerViewMember.layoutManager = LinearLayoutManager(mActivity)

            mAdapter= DialogUserdapter(mActivity,listMenbertList,upDade = { userid: Long, position: Int ->
                pwInterface?.join(userid,listMenbertList[position])
            })
            recyclerViewMember.setAdapter(mAdapter)
            recyclerViewMember.setSmartRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    listMenbertList.clear()
                    pwInterface?.refresh()
                }
                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    pwInterface?.more()
                }
            })
            recyclerViewMember.overScrollMode = View.OVER_SCROLL_NEVER//去掉边缘的光晕效果
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