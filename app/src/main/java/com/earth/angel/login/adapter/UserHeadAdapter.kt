package com.earth.angel.login.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R

class UserHeadAdapter(
    data: MutableList<Int>?,
    var addImg: () -> Unit = {},
    var upDade: (position: Int) -> Unit = {}
) :
    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_user_head, data) {

    override fun convert(holder: BaseViewHolder, item: Int) {
        val ivHead: ImageView = holder.getView(R.id.iv_thumb)
        item?.let{
            Glide.with(context)
                .load(it)
                .into(ivHead)
        }

    }

}
