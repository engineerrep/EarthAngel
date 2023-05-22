package com.earth.libhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.GroupStepEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R


class GroupStepAdapter(
    mContext: Context?,
    commentEntity: MutableList<GroupStepEntity>?,
    var upDade: (str: GroupStepEntity) -> Unit = {}
) : BaseQuickAdapter<GroupStepEntity, BaseViewHolder>(
    R.layout.item_libmine_groupstep,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: GroupStepEntity) {
        item?.name?.let {
            holder.setText(R.id.ivName,it)
        }
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        item?.photo?.let {
            ivHeader?.setImageResource(it)
        }
        var ivDown: TextView? = holder.getView(R.id.ivDown)
        var llDown: LinearLayout? = holder.getView(R.id.llDown)

        if(item.down){
            ivDown?.visibility=View.GONE
            llDown?.visibility=View.VISIBLE
        }else{
            ivDown?.visibility=View.VISIBLE
            llDown?.visibility=View.GONE
        }
    }


}