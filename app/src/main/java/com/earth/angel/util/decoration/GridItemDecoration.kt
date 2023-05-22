package com.mason.common.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(var space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        when (position % 3) {
            0 -> {
                outRect.left = space / 2
                outRect.right = space / 2
                outRect.top = space / 2  //TODO top 和bottom 应该根据行数来控制,懒得写了
                outRect.bottom = space / 2
            }
            1 -> {
                outRect.left = space / 2
                outRect.right = space / 2
                outRect.top = space / 2
                outRect.bottom = space / 2
            }
            2 -> {
                outRect.left = space / 2
                outRect.right = space / 2
                outRect.top = space / 2
                outRect.bottom = space / 2
            }
        }
    }
}