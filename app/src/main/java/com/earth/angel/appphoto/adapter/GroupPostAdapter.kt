package com.earth.angel.appphoto.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R

class GroupPostAdapter(list: MutableList<GroupEntity>,
                       var mDelete: (mGroupEntity: GroupEntity) -> Unit = {}) :
    BaseQuickAdapter<GroupEntity, BaseViewHolder>(R.layout.item_group_post, list) {
    override fun convert(holder: BaseViewHolder, item: GroupEntity) {
        val ivContent: ShapedImageView = holder.getView(R.id.ivGroupHead)
        val ivGroupDelete: ImageView = holder.getView(R.id.ivGroupDelete)

        item.picUrl?.let {
            Glide.with(context)
                .load(it)
                .placeholder(R.mipmap.group_comment)
                .into(ivContent!!)
        }
        item?.communityName?.let {
            holder.setText(R.id.ivGroupName,it)
        }
        ivGroupDelete.setOnClickListener {
            mDelete(item)
        }
    }
}