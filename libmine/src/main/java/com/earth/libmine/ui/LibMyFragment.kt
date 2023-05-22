package com.earth.libmine.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.util.AppUtils.dp2px
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.LogUtils
import com.earth.libmine.R
import com.earth.libmine.adapter.MineUserListGiftListAdapter
import com.earth.libmine.databinding.FragmentLibMinenewBinding
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnMultiListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class LibMyFragment : BaseFragment<FragmentLibMinenewBinding>() {

    private val mMineModle by viewModel<LibMineModle>()
    private var pageNum: Int = 1
    private var listHistory: ArrayList<BaseGiftEntity> = ArrayList()
    private var mHistoryAdapter: MineUserListGiftListAdapter? = null


    private var mOffset = 0
    private var mScrollY = 0

    override fun getLayoutId(): Int = R.layout.fragment_lib_minenew

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            mHistoryAdapter = MineUserListGiftListAdapter(activity, listHistory)
            mHistoryAdapter?.setOnItemClickListener { _, _, position ->
                if (BaseDateUtils.isFastClick()) {
                    ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                        .withString("id", listHistory[position].releaseRecordId)
                        .withString("userid", listHistory[position].userId)
                        .navigation()
                }
            }
            var gridLayoutManager = GridLayoutManager(activity, 2)
            libMineHomeRlv.layoutManager = gridLayoutManager
            libMineHomeRlv.adapter = mHistoryAdapter
            refreshLayout.setOnMultiListener(object : OnMultiListener {
                override fun onRefresh(refreshLayout: RefreshLayout) {
                    refreshLayout.finishRefresh(1000)
                }

                override fun onLoadMore(refreshLayout: RefreshLayout) {
                    refreshLayout.finishLoadMore(1000)
                }

                @SuppressLint("RestrictedApi")
                override fun onStateChanged(
                    refreshLayout: RefreshLayout,
                    oldState: RefreshState,
                    newState: RefreshState
                ) {

                }

                override fun onHeaderMoving(
                    header: RefreshHeader?,
                    isDragging: Boolean,
                    percent: Float,
                    offset: Int,
                    headerHeight: Int,
                    maxDragHeight: Int
                ) {
                    mOffset = offset
                    parallax.translationY = (mOffset - mScrollY).toFloat()
                    LogUtils.error("mOffset"+mOffset)

                }

                override fun onHeaderReleased(
                    header: RefreshHeader?,
                    headerHeight: Int,
                    maxDragHeight: Int
                ) {
                }

                override fun onHeaderStartAnimator(
                    header: RefreshHeader?,
                    headerHeight: Int,
                    maxDragHeight: Int
                ) {
                }

                override fun onHeaderFinish(header: RefreshHeader?, success: Boolean) {
                }

                override fun onFooterMoving(
                    footer: RefreshFooter?,
                    isDragging: Boolean,
                    percent: Float,
                    offset: Int,
                    footerHeight: Int,
                    maxDragHeight: Int
                ) {

                }

                override fun onFooterReleased(
                    footer: RefreshFooter?,
                    footerHeight: Int,
                    maxDragHeight: Int
                ) {
                }

                override fun onFooterStartAnimator(
                    footer: RefreshFooter?,
                    footerHeight: Int,
                    maxDragHeight: Int
                ) {
                }

                override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {
                }
            })

            scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
                private var lastScrollY = 0
                private val h: Int = dp2px(170F)


                override fun onScrollChange(
                    v: NestedScrollView?,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
                    var scrollY = scrollY
                    if (lastScrollY < h) {
                        scrollY = Math.min(h, scrollY)
                        mScrollY = Math.min(scrollY, h)

                        parallax.translationY = (mOffset - mScrollY).toFloat()
                    }
                    lastScrollY = scrollY
                    LogUtils.error("lastScrollY"+lastScrollY)
                }
            })
        }
        getGiftList()
    }
    private fun getGiftList() {
        launch {
            BaseApplication.myBaseUser?.userId?.let {
                mMineModle.userPageGiftList(
                    CommonRequest(
                        pageNum = pageNum,
                        pageSize = 10,
                        userId = it.toLong(),
                        releaseStatus = CommonRequest.EXCHANGED
                    )
                )
                    .onStart {
                    }.catch {
                    }.onCompletion {
                    }.collectLatest {
                        mBinding?.refreshLayout?.let {
                            it.finishRefresh(true)
                            it.finishLoadMore(true)
                        }
                        if (it.isOk(activity)) {
                            it.data?.list?.let {
                                listHistory.addAll(it)
                                mHistoryAdapter?.notifyDataSetChanged()
                            }

                        }
                    }
            }
        }
    }


}