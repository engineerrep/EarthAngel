package com.earth.libhome.adapter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.entity.GroupEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R

class GroupPageAdapter(list: MutableList<GroupEntity>) :
    BaseQuickAdapter<GroupEntity, BaseViewHolder>(R.layout.item_grouppage, list) {
    override fun convert(holder: BaseViewHolder, item: GroupEntity) {
        var itemHead1: ShapedImageView? = holder.getView(R.id.itemHead1)
        var groupMineIv: ImageView? = holder.getView(R.id.groupMineIv)

        item.let {
            holder.setText(R.id.itemName, it.communityName)
            if(it.userNum.toString()=="1"||it.userNum.toString()=="0"){
                holder.setText(R.id.tvNum, it.userNum.toString()+" people")
            }else{
                holder.setText(R.id.tvNum, it.userNum.toString()+" peoples")
            }
            var strItem = if (it.itemNum == 0 || it.itemNum == 1) {
                it.itemNum.toString()+" New Product"
            } else {
                it.itemNum.toString()+" New Products"
            }
            holder.setText(R.id.tvItemNum, strItem)
            it.picUrl?.let {
                Glide.with(itemHead1!!.context)
                    .load(it)
                    .placeholder(R.mipmap.group_default)
                    .into(itemHead1!!)
            }
            it.isCreator?.let{
                if(it){
                    groupMineIv?.visibility= View.VISIBLE
                }else{
                    groupMineIv?.visibility= View.GONE
                }
            }
        }

    }
}