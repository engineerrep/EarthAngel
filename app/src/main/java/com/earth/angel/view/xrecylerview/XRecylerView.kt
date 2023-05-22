package com.earth.angel.view.xrecylerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.earth.angel.R

import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

class XRecyclerView : FrameLayout {
    var mContext: Context? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SmartRefreshLayout
    private var adapter: RecyclerView.Adapter<*>? = null
    private var recycleViewDivider: RecyclerView.ItemDecoration? = null

    // 当前加载状态，默认为加载完成
    private var loadState = 2

    constructor(context: Context) : super(context) {}

    private fun initData() {

    }


    fun setNeedScrollBar(need: Boolean) {
        recyclerView.isVerticalScrollBarEnabled = need;
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        mContext = context
        initView()
        initData()
        initListener()
    }

    private fun initListener() {
    }

    fun setSmartRefreshLoadMoreListener(listener: OnRefreshLoadMoreListener) {
        refreshLayout?.setOnRefreshLoadMoreListener(listener)
    }

    fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        refreshLayout?.setOnRefreshListener(onRefreshListener)
    }

    private fun initView() {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.base_recyclerview, this)
        recyclerView = view.findViewById(R.id.recyclerView)
        refreshLayout = view.findViewById(R.id.refreshLayout)

        refreshLayout.setEnableRefresh(true)
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false)
    }

    var layoutManager: RecyclerView.LayoutManager?
        get() = recyclerView.layoutManager
        set(layoutManager) {
            recyclerView.layoutManager = layoutManager
        }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        adapter?.run {
//            setHasStableIds(true)
            this@XRecyclerView.adapter = adapter
            recyclerView.adapter = adapter
        }


    }

    fun setXLayoutManager(layout: RecyclerView.LayoutManager?) {
        recyclerView?.layoutManager = layout;
    }

    fun getAdapter(): RecyclerView.Adapter<*>? {
        return adapter
    }

    private fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }


    fun closeRefreshAnimal() {
        refreshLayout.finishRefresh(refreshLoadDelay)
    }

    fun openRefreshAnimal() {
        refreshLayout.autoRefresh(refreshLoadDelay)
    }

    fun setLoadState(state: Int) {
        loadState = state
        when (state) {
            LOADING -> {
                refreshLayout.autoLoadMore(refreshLoadDelay)
            }
            LOADING_COMPLETE -> {
                refreshLayout.finishLoadMore(refreshLoadDelay)
            }
            LOADING_END -> {
                refreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
        notifyDataSetChanged()
    }

    fun finishRefresh(isSuccess: Boolean) {
        refreshLayout.finishRefresh(isSuccess)
    }

    fun finishLoadMore(isSuccess: Boolean) {
        refreshLayout.finishLoadMore(isSuccess)
    }

    fun setLoadMoreEnable(loadMoreEnable: Boolean) {
        refreshLayout.setEnableLoadMore(loadMoreEnable)
    }

    fun setRefreshEnable(refreshEnable: Boolean) {
        refreshLayout.setEnableRefresh(refreshEnable)
    }

    fun getRefreshLayout(): RefreshLayout? {
        return refreshLayout
    }

    fun setEnableFooterFollowWhenNoMoreData(enableFooterFollowWhenNoMoreData: Boolean) {
        refreshLayout.setEnableFooterFollowWhenNoMoreData(enableFooterFollowWhenNoMoreData)
    }

    fun scrollToPosition(position: Int) {
        recyclerView.scrollToPosition(position)
    }

    fun smoothScrollToPosition(position: Int) {
        recyclerView.smoothScrollToPosition(position)
    }


    fun addItemDecoration(recycleViewDivider: ItemDecoration?) {
        if (recycleViewDivider != null) {
            recyclerView.addItemDecoration(recycleViewDivider)
        }
    }


    companion object {
        // 正在加载
        const val LOADING = 1

        // 加载完成
        const val LOADING_COMPLETE = 2

        // 加载到底
        const val LOADING_END = 3
        const val refreshLoadDelay = 0
    }
}

