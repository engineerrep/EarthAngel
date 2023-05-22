package com.earth.libhome.adapter

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R

class GroupAssociatedAdapter(list: MutableList<GroupEntity>) :
    BaseQuickAdapter<GroupEntity, BaseViewHolder>(R.layout.item_groupdetalis_post, list) {
    override fun convert(holder: BaseViewHolder, item: GroupEntity) {
        val ivContent: ShapedImageView = holder.getView(R.id.ivGroupHead)
        val llGroupBg: LinearLayout = holder.getView(R.id.llGroupBg)
        val ivGroupName: TextView = holder.getView(R.id.ivGroupName)

        item?.picUrl?.let {
            Glide.with(context)
                .load(it)
                .placeholder(R.mipmap.group_comment)
                .into(ivContent!!)
        }
        item?.communityName?.let {
            holder.setText(R.id.ivGroupName,it)
        }
        item?.isSelect?.let {
            if(!it){
                llGroupBg.setBackgroundResource(R.drawable.bg_image_best)
                ivGroupName.setTextColor(ContextCompat.getColor(BaseApplication.instance, R.color.text_black))
            }else{
                llGroupBg.setBackgroundResource(R.drawable.bg_themcolor_16)
                ivGroupName.setTextColor(ContextCompat.getColor(BaseApplication.instance, R.color.base_white))
            }
        }

    }
}