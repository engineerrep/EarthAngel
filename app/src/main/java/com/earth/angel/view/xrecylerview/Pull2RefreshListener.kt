package com.earth.angel.view.xrecylerview

import androidx.recyclerview.widget.RecyclerView

interface Pull2RefreshListener {
    fun onRefresh()
    fun onLoadMore()
    fun onScroll(recyclerView: RecyclerView?, dx: Int, dy: Int)
}