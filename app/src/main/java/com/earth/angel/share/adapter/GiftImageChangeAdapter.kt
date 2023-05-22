package com.earth.angel.share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R

class GiftImageChangeAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<String>?,
    var upDade: () -> Unit = {}
) : BaseQuickAdapter<String, BaseViewHolder>(
    R.layout.item_gift_change,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: String) {

        var ivChange: ImageView? = holder.getView(R.id.ivChange)

        item?.let {
            Glide.with(context)
                .load(item)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivChange!!)
        }



    }


}