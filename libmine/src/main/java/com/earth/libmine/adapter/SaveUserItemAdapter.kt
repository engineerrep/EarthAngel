package com.earth.libmine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.baseentity.BaseUser

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R


class SaveUserItemAdapter(
    mContext: Context?,
    commentEntity: MutableList<BaseUser>?,
    var upDade: (str: BaseUser) -> Unit = {}
) : BaseQuickAdapter<BaseUser, BaseViewHolder>(
    R.layout.item_libmine_save_user,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseUser) {
        holder.setText(R.id.ivLibMineName,item.nickName)
        var ivHead: ImageView? = holder.getView(R.id.ivlibMineHeadImage)
        var ivIntroduce: TextView? = holder.getView(R.id.ivLibMinedIntroduce)

        item?.headImgUrl?.let {
                Glide.with(ivHead!!.context)
                    .load(it)
                    .into(ivHead)
        }
        item?.postNumber?.let {
            if(it=="0"||it=="1"){
                ivIntroduce?.text= "$it product waiting to be given"
            }else{
                ivIntroduce?.text= "$it products waiting to be given"
            }


        }

    }


}