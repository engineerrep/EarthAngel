package com.earth.libhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.entity.PicTureEntity
import com.earth.libhome.R


class HomeGiftDetailAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<String>?,
    var upDade: (galleryModel: String) -> Unit = {}
) : BaseQuickAdapter<String, BaseViewHolder>(
    R.layout.item_libhome_giftdetalis,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    companion object {
        var selectPosition = -1
    }
    override fun convert(holder: BaseViewHolder, item: String) {
        var ivHeader: ImageView? = holder.getView(R.id.ItemPublishEdit)
        item?.let {
            Glide.with(context)
                .load(it)
                .placeholder(R.mipmap.base_img_loding)
                .into(ivHeader!!)
        }

    }


}